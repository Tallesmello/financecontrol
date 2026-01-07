package br.com.talles.financecontrol;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.talles.financecontrol.model.Despesa;
import br.com.talles.financecontrol.viewmodel.DespesaViewModel;

public class MainActivity extends AppCompatActivity {

    private EditText edtValor, edtDescricao, edtPesquisa, edtData, edtVencimento;
    private Button btnAdicionar;
    private Spinner spnCategoria, spnFiltroCategoria;
    private RecyclerView recyclerDespesas;
    private TextView txtResumo;
    private final List<Despesa> listaFiltrada = new ArrayList<>();
    private DespesaAdapter adapter;
    private DespesaViewModel viewModel;

    // CICLO DE VIDA

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        setupRecyclerView();

        viewModel = new ViewModelProvider(this).get(DespesaViewModel.class);

        observarDespesas();
        observarResumo();
        observarMensagem();
        observarDatas();

        btnAdicionar.setOnClickListener(v -> adicionarDespesa());
        edtData.setOnClickListener(v -> abrirDatePicker());
        edtVencimento.setOnClickListener(v -> abrirDatePickerVencimento());

        configurarPesquisa();
        configurarFiltroCategoria();

    }

    // OBSERVADOR

    private void observarDespesas() {
        viewModel.getDespesas().observe(this, despesas -> {
            listaFiltrada.clear();
            listaFiltrada.addAll(despesas);
            adapter.notifyDataSetChanged();

        });
    }

    private void observarResumo() {
        viewModel.getResumoFinanceiro().observe(this, resumo -> {
            txtResumo.setText(resumo);
        });
    }

    private void observarMensagem() {
        viewModel.getMensagem().observe(this, event -> {
            if (event == null) return;

            String msg = event.getContentIfNotHandled();
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void observarDatas() {

        viewModel.getTextoData().observe(this, texto -> {
            edtData.setText(texto);
        });

        viewModel.getTextoVencimento().observe(this, texto -> {
            edtVencimento.setText(texto);
        });
    }

    // SETUP

    private void bindViews() {
        edtValor = findViewById(R.id.edtValor);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtPesquisa = findViewById(R.id.edtPesquisa);
        edtData = findViewById(R.id.edtData);
        edtVencimento = findViewById(R.id.edtVencimento);

        btnAdicionar = findViewById(R.id.btnAdicionar);

        spnCategoria = findViewById(R.id.spnCategoria);
        spnFiltroCategoria = findViewById(R.id.spnFiltroCategoria);

        recyclerDespesas = findViewById(R.id.recyclerDespesas);
        txtResumo = findViewById(R.id.txtResumo);
    }

    private void setupRecyclerView() {
        adapter = new DespesaAdapter(listaFiltrada);
        recyclerDespesas.setLayoutManager(new LinearLayoutManager(this));
        recyclerDespesas.setAdapter(adapter);
    }

    private void adicionarDespesa() {

        viewModel.adicionarDespesa(
                edtValor.getText().toString(),
                edtDescricao.getText().toString(),
                spnCategoria.getSelectedItem().toString(),
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

    // DATE PICKERS

    private void abrirDatePicker() {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    viewModel.setDataSelecionada(y, m, d);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void abrirDatePickerVencimento() {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    viewModel.setDataVencimentoSelecionada(y, m, d);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // FILTROS

    private void configurarPesquisa() {
        edtPesquisa.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ✅ agora o filtro é feito no ViewModel
                viewModel.setTextoPesquisa(s.toString());
            }
        });
    }

    private void configurarFiltroCategoria() {
        spnFiltroCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                // ✅ agora o filtro é feito no ViewModel
                viewModel.setCategoriaFiltro(p.getItemAtPosition(pos).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
