package br.com.talles.financecontrol.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

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

    private static final String CATEGORIA_TODAS = "Todas";
    private final DespesaDao despesaDao;
    private final ExecutorService executor;
    private final String userId;
    private final MutableLiveData<List<Despesa>> todasDespesas = new MutableLiveData<>();
    private final MutableLiveData<List<Despesa>> despesasFiltradas = new MutableLiveData<>();
    private final MutableLiveData<String> resumoFinanceiro = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> mensagem = new MutableLiveData<>();
    private final MutableLiveData<Long> dataSelecionada = new MutableLiveData<>();
    private final MutableLiveData<Long> dataVencimentoSelecionada = new MutableLiveData<>();
    private final MutableLiveData<String> textoData = new MutableLiveData<>();
    private final MutableLiveData<String> textoVencimento = new MutableLiveData<>();
    private String textoPesquisa = "";
    private String categoriaFiltro = CATEGORIA_TODAS;
    private final MutableLiveData<Map<String, Integer>> contadorCategorias = new MutableLiveData<>();

    public enum FiltroPeriodo {
        TODOS,
        HOJE,
        MES,
        INTERVALO
    }

    private FiltroPeriodo filtroPeriodo = FiltroPeriodo.TODOS;
    private Long dataInicioFiltro = null;
    private Long dataFimFiltro = null;

    public void setFiltroHoje() {
        filtroPeriodo = FiltroPeriodo.HOJE;
        aplicarFiltros();
    }

    public void setFiltroMesAtual() {
        filtroPeriodo = FiltroPeriodo.MES;
        aplicarFiltros();
    }

    public void setFiltroTodos() {
        filtroPeriodo = FiltroPeriodo.TODOS;
        aplicarFiltros();
    }

    public void setFiltroIntervalo(Long inicio, Long fim) {
        filtroPeriodo = FiltroPeriodo.INTERVALO;
        dataInicioFiltro = inicio;
        dataFimFiltro = fim;
        aplicarFiltros();
    }

    public DespesaViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        despesaDao = db.despesaDao();
        executor = Executors.newSingleThreadExecutor();

        definirDataHoje();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        carregarDespesas();
    }

    // ================= GETTERS =================
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

    public LiveData<Map<String, Integer>> getContadorCategorias() {
        return contadorCategorias;
    }

    // ================= DATABASE =================
    private void carregarDespesas() {
        executor.execute(() -> {
            List<Despesa> lista = despesaDao.listarPorUsuario(userId);
            todasDespesas.postValue(lista);
            calcularContadorCategorias(lista);
            aplicarFiltrosComLista(lista);
        });
    }

    // ================= FILTROS =================
    private boolean matchPeriodo(Despesa d) {

        if (filtroPeriodo == FiltroPeriodo.TODOS) {
            return true;
        }

        Calendar dataDespesa = Calendar.getInstance();
        dataDespesa.setTimeInMillis(d.getData());

        Calendar hoje = Calendar.getInstance();

        if (filtroPeriodo == FiltroPeriodo.HOJE) {
            return dataDespesa.get(Calendar.YEAR) == hoje.get(Calendar.YEAR)
                    && dataDespesa.get(Calendar.DAY_OF_YEAR) == hoje.get(Calendar.DAY_OF_YEAR);
        }

        if (filtroPeriodo == FiltroPeriodo.MES) {
            return dataDespesa.get(Calendar.YEAR) == hoje.get(Calendar.YEAR)
                    && dataDespesa.get(Calendar.MONTH) == hoje.get(Calendar.MONTH);
        }

        if (filtroPeriodo == FiltroPeriodo.INTERVALO) {
            return d.getData() >= dataInicioFiltro && d.getData() <= dataFimFiltro;
        }

        return true;
    }


    private void aplicarFiltrosComLista(List<Despesa> origem) {

        List<Despesa> resultado = new ArrayList<>();

        for (Despesa d : origem) {

            boolean matchTexto =
                    textoPesquisa == null || textoPesquisa.isEmpty()
                            || d.getDescricao().toLowerCase()
                            .contains(textoPesquisa.toLowerCase());

            boolean matchCategoria =
                    categoriaFiltro.equals(CATEGORIA_TODAS)
                            || d.getCategoria().equals(categoriaFiltro);

            if (matchTexto && matchCategoria && matchPeriodo(d)) {
                resultado.add(d);
            }
        }

        despesasFiltradas.postValue(resultado);
        calcularResumo(resultado);
    }

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
        if (origem != null) {
            aplicarFiltrosComLista(origem);
        }
    }

    // ================= ADD DESPESA =================
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

        double valor;

        try {
            valor = Double.parseDouble(valorTexto.replace(",", "."));
        } catch (NumberFormatException e) {
            mensagem.postValue(new Event<>("Valor inválido"));
            return;
        }

        Despesa despesa = new Despesa(
                valor,
                descricao,
                categoria,
                dataSelecionada,
                dataVencimentoSelecionada,
                userId
        );

        executor.execute(() -> {
            despesaDao.inserir(despesa);
            mensagem.postValue(new Event<>("Despesa adicionada"));
            carregarDespesas();
        });
    }

    // ================= DATAS =================
    public void definirDataHoje() {
        Calendar hoje = Calendar.getInstance();
        definirData(
                hoje.get(Calendar.YEAR),
                hoje.get(Calendar.MONTH),
                hoje.get(Calendar.DAY_OF_MONTH)
        );
    }

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

    public void setDataVencimentoSelecionada(int y, int m, int d) {
        Calendar data = Calendar.getInstance();
        data.set(y, m, d, 0, 0, 0);

        long millis = data.getTimeInMillis();
        dataVencimentoSelecionada.setValue(millis);
        textoVencimento.setValue(d + "/" + (m + 1) + "/" + y);
    }

    // ================= RESUMO =================
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

    private void calcularContadorCategorias(List<Despesa> lista) {

        Map<String, Integer> contador = new HashMap<>();

        for (Despesa d : lista) {
            String categoria = d.getCategoria();
            contador.put(categoria, contador.getOrDefault(categoria, 0) + 1);
        }

        contadorCategorias.postValue(contador);
    }

}
