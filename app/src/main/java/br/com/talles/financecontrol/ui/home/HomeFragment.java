package br.com.talles.financecontrol.ui.home;

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
import java.util.List;

import br.com.talles.financecontrol.DespesaAdapter;
import br.com.talles.financecontrol.MainActivity;
import br.com.talles.financecontrol.R;
import br.com.talles.financecontrol.model.Despesa;
import br.com.talles.financecontrol.viewmodel.DespesaViewModel;

public class HomeFragment extends Fragment {

    private EditText edtPesquisa;
    private Spinner spnFiltroCategoria;
    private RecyclerView recyclerDespesas;
    private TextView txtResumo;
    private DespesaAdapter adapter;
    private DespesaViewModel viewModel;
    private TextView txtVazio;
    private TextView txtContadorCategorias;
    private Button btnLogout;
    private Button btnHoje, btnMes, btnTodos;
    private final List<Despesa> listaFiltrada = new ArrayList<>();

    public HomeFragment() {
        // construtor vazio obrigatório
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
        observarContadorCategorias();

        configurarPesquisa();
        configurarFiltroCategoria();

        btnHoje.setOnClickListener(v -> viewModel.setFiltroHoje());
        btnMes.setOnClickListener(v -> viewModel.setFiltroMesAtual());
        btnTodos.setOnClickListener(v -> viewModel.setFiltroTodos());

        btnLogout.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).logout();
        });

        return view;
    }

    /* ================= OBSERVERS ================= */

    private void observarDespesas() {
        viewModel.getDespesas().observe(getViewLifecycleOwner(), despesas -> {
            listaFiltrada.clear();
            listaFiltrada.addAll(despesas);
            adapter.notifyDataSetChanged();

            if (despesas == null || despesas.isEmpty()) {
                txtVazio.setVisibility(View.VISIBLE);
                recyclerDespesas.setVisibility(View.GONE);
            } else {
                txtVazio.setVisibility(View.GONE);
                recyclerDespesas.setVisibility(View.VISIBLE);
            }
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

    private void observarContadorCategorias() {
        viewModel.getContadorCategorias().observe(getViewLifecycleOwner(), contador -> {

            if (contador == null || contador.isEmpty()) {
                txtContadorCategorias.setText("");
                return;
            }

            StringBuilder texto = new StringBuilder();

            for (String categoria : contador.keySet()) {
                texto.append(categoria)
                        .append(" (")
                        .append(contador.get(categoria))
                        .append(")  •  ");
            }

            // remove o último separador
            if (texto.length() >= 5) {
                texto.setLength(texto.length() - 5);
            }

            txtContadorCategorias.setText(texto.toString());
        });
    }

    /* ================= SETUP ================= */

    private void bindViews(View view) {
        edtPesquisa = view.findViewById(R.id.edtPesquisa);
        spnFiltroCategoria = view.findViewById(R.id.spnFiltroCategoria);
        recyclerDespesas = view.findViewById(R.id.recyclerDespesas);
        txtResumo = view.findViewById(R.id.txtResumo);
        txtVazio = view.findViewById(R.id.txtVazio);
        txtContadorCategorias = view.findViewById(R.id.txtContadorCategorias);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnHoje = view.findViewById(R.id.chipHoje);
        btnMes = view.findViewById(R.id.chipMes);
        btnTodos = view.findViewById(R.id.chipTodos);
    }

    private void setupRecyclerView() {
        adapter = new DespesaAdapter(listaFiltrada);
        recyclerDespesas.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        recyclerDespesas.setAdapter(adapter);
    }

    /* ================= FILTROS ================= */

    private void configurarPesquisa() {
        edtPesquisa.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setTextoPesquisa(s.toString());
            }
        });
    }

    private void configurarFiltroCategoria() {
        List<String> categorias = new ArrayList<>();
        categorias.add("Todas");
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
        spnFiltroCategoria.setAdapter(adapter);

        spnFiltroCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setCategoriaFiltro(
                        parent.getItemAtPosition(position).toString()
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
