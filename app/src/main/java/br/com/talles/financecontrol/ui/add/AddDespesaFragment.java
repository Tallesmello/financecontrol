package br.com.talles.financecontrol.ui.add;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.talles.financecontrol.R;
import br.com.talles.financecontrol.viewmodel.DespesaViewModel;

public class AddDespesaFragment extends Fragment {

    private EditText edtValor, edtDescricao, edtData, edtVencimento;
    private Spinner spnCategoria;
    private Button btnAdicionar;

    private DespesaViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_add_despesa, container, false);

        bindViews(view);
        configurarSpinner();

        viewModel = new ViewModelProvider(requireActivity())
                .get(DespesaViewModel.class);

        viewModel.getTextoData().observe(getViewLifecycleOwner(), texto ->
                edtData.setText(texto)
        );

        viewModel.getTextoVencimento().observe(getViewLifecycleOwner(), texto ->
                edtVencimento.setText(texto)
        );

        viewModel.getMensagem().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;

            String msg = event.getContentIfNotHandled();
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        edtData.setOnClickListener(v -> abrirDatePicker(false));
        edtVencimento.setOnClickListener(v -> abrirDatePicker(true));

        btnAdicionar.setOnClickListener(v -> salvarDespesa());

        return view;
    }

    private void bindViews(View view) {
        edtValor = view.findViewById(R.id.edtValor);
        edtDescricao = view.findViewById(R.id.edtDescricao);
        edtData = view.findViewById(R.id.edtData);
        edtVencimento = view.findViewById(R.id.edtVencimento);
        spnCategoria = view.findViewById(R.id.spnCategoria);
        btnAdicionar = view.findViewById(R.id.btnAdicionar);
    }

    private void configurarSpinner() {
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
    }

    private void abrirDatePicker(boolean vencimento) {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(
                requireContext(),
                (view, y, m, d) -> {
                    if (vencimento) {
                        viewModel.setDataVencimentoSelecionada(y, m, d);
                    } else {
                        viewModel.setDataSelecionada(y, m, d);
                    }
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void salvarDespesa() {

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
        viewModel.definirDataHoje();
    }

    private void limparCampos() {
        edtValor.setText("");
        edtDescricao.setText("");
        edtData.setText("");
        edtVencimento.setText("");
        spnCategoria.setSelection(0);
    }
}
