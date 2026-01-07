package br.com.talles.financecontrol;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Date;

import br.com.talles.financecontrol.model.Despesa;

public class DespesaAdapter extends RecyclerView.Adapter<DespesaAdapter.ViewHolder> {

    private List<Despesa> despesas;// A lista de objetos que contém os dados

    public DespesaAdapter(List<Despesa> despesas) {
        this.despesas = despesas;// Construtor que recebe a lista de despesas
    }

    public void atualizarLista(List<Despesa> novaLista) {
        this.despesas = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DespesaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Transforma o arquivo XML (item_despesa) em um objeto de Visualização (View)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_despesa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DespesaAdapter.ViewHolder holder, int position) {

        Despesa despesa = despesas.get(position);

        holder.txtDescricao.setText(despesa.getDescricao());
        holder.txtValor.setText("R$ " + despesa.getValor());
        holder.txtCategoria.setText(despesa.getCategoria());

        // MOSTRAR A DATA
        bindData(holder, despesa);
        // Mostra vendimento se tiver
        bindVencimento(holder, despesa);

    }

    private static void bindData(@NonNull ViewHolder holder, Despesa despesa) {
        holder.txtData.setText(formatarData(despesa.getData()));
    }


    private static void bindVencimento(@NonNull ViewHolder holder, Despesa despesa) {
        if (despesa.getDataVencimento() != null) {
            holder.txtVencimento.setText(
                    "Vence em: " + formatarData(despesa.getDataVencimento())
            );
        } else {
            holder.txtVencimento.setText("");
        }
    }

    private static String formatarData(Long timestamp) {
        if (timestamp == null) return "";

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        return sdf.format(new Date(timestamp));
    }

    @Override
    public int getItemCount() {
        return despesas.size(); //Retorna o tamanho total da lista
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtDescricao;
        TextView txtValor;
        TextView txtCategoria;
        TextView txtData;
        TextView txtVencimento;




        public ViewHolder(@NonNull View itemView) {
            super(itemView); // Mapeia os componentes do layout XML para as variáveis correspondentes

            txtDescricao = itemView.findViewById(R.id.txtDescricao);
            txtValor = itemView.findViewById(R.id.txtValor);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);
            txtData = itemView.findViewById(R.id.txtData);
            txtVencimento = itemView.findViewById(R.id.txtVencimento);



        }
    }
}
