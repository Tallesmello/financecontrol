package br.com.talles.financecontrol.ui.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.talles.financecontrol.DespesaAdapter;
import br.com.talles.financecontrol.R;
import br.com.talles.financecontrol.model.Despesa;
import br.com.talles.financecontrol.viewmodel.DespesaViewModel;

public class HomeFragment extends Fragment {

    private EditText edtValor, edtDescricao, edtPesquisa, edtData, edtVencimento;
    private Button btnAdicionar;
    private Spinner spnCategoria, spnFiltroCategoria;
    private RecyclerView recyclerDespesas;
    private TextView txtResumo;

    private final List<Despesa> listaFiltrada = new ArrayList<>();
    private DespesaAdapter adapter;
    private DespesaViewModel viewModel;

    public HomeFragment() {
        // Construtor vazio obrigatório
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bindViews(view);
        setupRecyclerView();

        viewModel = new ViewModelProvider(requireActivity())
                .get(DespesaViewModel.class);

        observarDespesas();
        observarResumo();
        observarMensagem();
        observarDatas();

        btnAdicionar.setOnClickListener(v -> adicionarDespesa());
        edtData.setOnClickListener(v -> abrirDatePicker());
        edtVencimento.setOnClickListener(v -> abrirDatePickerVencimento());

        configurarPesquisa();
        configurarFiltroCategoria();
        configurarSpinners();

        return view;
    }

    /* ================= OBSERVERS ================= */

    private void observarDespesas() {
        viewModel.getDespesas().observe(getViewLifecycleOwner(), despesas -> {
            listaFiltrada.clear();
            listaFiltrada.addAll(despesas);
            adapter.notifyDataSetChanged();
        });
    }

    private void observarResumo() {
        viewModel.getResumoFinanceiro().observe(getViewLifecycleOwner(), resumo ->
                txtResumo.setText(resumo)
        );
    }

    private void observarMensagem() {
        viewModel.getMensagem().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;

            String msg = event.getContentIfNotHandled();
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observarDatas() {
        viewModel.getTextoData().observe(getViewLifecycleOwner(),
                texto -> edtData.setText(texto));

        viewModel.getTextoVencimento().observe(getViewLifecycleOwner(),
                texto -> edtVencimento.setText(texto));
    }

    /* ================= SETUP ================= */

    private void bindViews(View view) {
        edtValor = view.findViewById(R.id.edtValor);
        edtDescricao = view.findViewById(R.id.edtDescricao);
        edtPesquisa = view.findViewById(R.id.edtPesquisa);
        edtData = view.findViewById(R.id.edtData);
        edtVencimento = view.findViewById(R.id.edtVencimento);

        btnAdicionar = view.findViewById(R.id.btnAdicionar);

        spnCategoria = view.findViewById(R.id.spnCategoria);
        spnFiltroCategoria = view.findViewById(R.id.spnFiltroCategoria);

        recyclerDespesas = view.findViewById(R.id.recyclerDespesas);
        txtResumo = view.findViewById(R.id.txtResumo);
    }

    private void setupRecyclerView() {
        adapter = new DespesaAdapter(listaFiltrada);
        recyclerDespesas.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        recyclerDespesas.setAdapter(adapter);
    }

    /* ================= AÇÕES ================= */

    private void adicionarDespesa() {

        String categoria = spnCategoria.getSelectedItem() != null
                ? spnCategoria.getSelectedItem().toString()
                : "Geral";

        viewModel.adicionarDespesa(
                edtValor.getText().toString(),
                edtDescricao.getText().toString(),
                categoria,
                viewModel.getDataSelecionada(),
                viewModel.getDataVencimentoSelecionada()
        );

        limparCampos();
    }

    private void limparCampos() {
        edtValor.setText("");
        edtDescricao.setText("");
        edtData.setText("");
        edtVencimento.setText("");
    }

    /* ================= DATE PICKERS ================= */

    private void abrirDatePicker() {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(
                requireContext(),
                (view, y, m, d) -> viewModel.setDataSelecionada(y, m, d),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void abrirDatePickerVencimento() {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(
                requireContext(),
                (view, y, m, d) -> viewModel.setDataVencimentoSelecionada(y, m, d),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    /* ================= FILTROS ================= */

    private void configurarPesquisa() {
        edtPesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setTextoPesquisa(s.toString());
            }
        });
    }

    private void configurarSpinners() {
        List<String> categorias = new ArrayList<>();
        categorias.add("Geral");
        categorias.add("Alimentação");
        categorias.add("Transporte");
        categorias.add("Lazer");
        categorias.add("Outros");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categorias
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnCategoria.setAdapter(adapter);
        spnFiltroCategoria.setAdapter(adapter);
    }


    private void configurarFiltroCategoria() {
        spnFiltroCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                viewModel.setCategoriaFiltro(
                        p.getItemAtPosition(pos).toString()
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
