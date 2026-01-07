package br.com.talles.financecontrol.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.talles.financecontrol.AppDatabase;
import br.com.talles.financecontrol.DespesaDao;
import br.com.talles.financecontrol.model.Despesa;
import br.com.talles.financecontrol.util.Event;

public class DespesaViewModel extends AndroidViewModel {

    private final DespesaDao despesaDao;
    private final ExecutorService executor;
    private final MutableLiveData<List<Despesa>> todasDespesas = new MutableLiveData<>();
    private final MutableLiveData<List<Despesa>> despesasFiltradas = new MutableLiveData<>();

    private final MutableLiveData<String> resumoFinanceiro = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> mensagem = new MutableLiveData<>();

    private final MutableLiveData<Long> dataSelecionada = new MutableLiveData<>();
    private final MutableLiveData<Long> dataVencimentoSelecionada = new MutableLiveData<>();

    private final MutableLiveData<String> textoData = new MutableLiveData<>();
    private final MutableLiveData<String> textoVencimento = new MutableLiveData<>();

    //FILTROS

    private String textoPesquisa = "";
    private String categoriaFiltro = "📋 Todas";

    //CONSTRUTOR

    public DespesaViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        despesaDao = db.despesaDao();

        executor = Executors.newSingleThreadExecutor();

        definirDataHoje();   // ✅ DATA PADRÃO = HOJE
        carregarDespesas();
    }

    //GETTERS

    public LiveData<List<Despesa>> getDespesas() {
        return despesasFiltradas;
    }

    public LiveData<String> getResumoFinanceiro() {
        return resumoFinanceiro;
    }

    public LiveData<Event<String>> getMensagem() {
        return mensagem;
    }

    public LiveData<String> getTextoData() {
        return textoData;
    }

    public LiveData<String> getTextoVencimento() {
        return textoVencimento;
    }

    public Long getDataSelecionada() {
        return dataSelecionada.getValue();
    }

    public Long getDataVencimentoSelecionada() {
        return dataVencimentoSelecionada.getValue();
    }

    //AÇÕE

    private void carregarDespesas() {
        executor.execute(() -> {
            List<Despesa> lista = despesaDao.listarTodas();
            todasDespesas.postValue(lista);
            aplicarFiltrosComLista(lista);
        });
    }

    private void aplicarFiltrosComLista(List<Despesa> origem) {

        List<Despesa> resultado = new ArrayList<>();

        for (Despesa d : origem) {

            boolean matchTexto =
                    d.getDescricao().toLowerCase()
                            .contains(textoPesquisa.toLowerCase());

            boolean matchCategoria =
                    categoriaFiltro.equals("📋 Todas")
                            || d.getCategoria().equals(categoriaFiltro);

            if (matchTexto && matchCategoria) {
                resultado.add(d);
            }
        }

        despesasFiltradas.postValue(new ArrayList<>(resultado));
        calcularResumo(resultado);
    }


    public void adicionarDespesa(
            String valorTexto,
            String descricao,
            String categoria,
            Long dataSelecionada,
            Long dataVencimentoSelecionada
    ) {

        if (valorTexto == null || valorTexto.isEmpty()
                || descricao == null || descricao.isEmpty()) {
            mensagem.postValue(new Event<>("Preencha todos os campos"));
            return;
        }

        if (dataSelecionada == null) {
            mensagem.postValue(new Event<>("Data inválida"));
            return;
        }

        double valor = Double.parseDouble(valorTexto);

        Despesa despesa = new Despesa(
                valor,
                descricao,
                categoria,
                dataSelecionada,
                dataVencimentoSelecionada
        );

        executor.execute(() -> {
            despesaDao.inserir(despesa);
            mensagem.postValue(new Event<>("Despesa adicionada"));
            carregarDespesas();
        });
    }

    // Data automática (HOJE)
    public void definirDataHoje() {
        Calendar hoje = Calendar.getInstance();
        definirData(
                hoje.get(Calendar.YEAR),
                hoje.get(Calendar.MONTH),
                hoje.get(Calendar.DAY_OF_MONTH)
        );
    }

    //Data manual (DatePicker)
    public void setDataSelecionada(int y, int m, int d) {
        definirData(y, m, d);
    }

    private void definirData(int y, int m, int d) {
        Calendar data = Calendar.getInstance();
        data.set(y, m, d, 0, 0, 0);

        long millis = data.getTimeInMillis();

        dataSelecionada.setValue(millis);
        textoData.setValue(d + "/" + (m + 1) + "/" + y);
    }

    //DATA DE VENCIMENTO (OPCIONAL)

    public void setDataVencimentoSelecionada(int y, int m, int d) {
        Calendar data = Calendar.getInstance();
        data.set(y, m, d, 0, 0, 0);

        long millis = data.getTimeInMillis();

        dataVencimentoSelecionada.setValue(millis);
        textoVencimento.setValue(d + "/" + (m + 1) + "/" + y);
    }

    //FILTROS

    public void setTextoPesquisa(String texto) {
        textoPesquisa = texto;
        aplicarFiltros();
    }

    public void setCategoriaFiltro(String categoria) {
        categoriaFiltro = categoria;
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        List<Despesa> origem = todasDespesas.getValue();
        if (origem == null) return;

        aplicarFiltrosComLista(origem);
    }

    // RESUMO FINANCEIRO

    private void calcularResumo(List<Despesa> lista) {

        double total = 0;
        Map<String, Double> mapa = new HashMap<>();

        for (Despesa d : lista) {
            total += d.getValor();
            mapa.put(
                    d.getDescricao(),
                    mapa.getOrDefault(d.getDescricao(), 0.0) + d.getValor()
            );
        }

        String maiorGasto = "Nenhum";
        double maiorValor = 0;

        for (Map.Entry<String, Double> e : mapa.entrySet()) {
            if (e.getValue() > maiorValor) {
                maiorValor = e.getValue();
                maiorGasto = e.getKey();
            }
        }

        String resumo =
                "Total gasto: R$ " + total +
                        "\nMaior gasto: " + maiorGasto + " (R$ " + maiorValor + ")";

        resumoFinanceiro.postValue(resumo);
    }
}
