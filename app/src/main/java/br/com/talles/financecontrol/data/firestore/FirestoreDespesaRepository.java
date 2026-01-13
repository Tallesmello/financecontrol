package br.com.talles.financecontrol.data.firestore;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import br.com.talles.financecontrol.model.Despesa;
public class FirestoreDespesaRepository {

    private final FirebaseFirestore firestore;
    private final String userId;

    public FirestoreDespesaRepository() {
        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * PREPARAÇÃO FUTURA
     * Não será chamado agora
     */
    public void salvarDespesa(Despesa despesa) {

        Map<String, Object> data = new HashMap<>();
        data.put("valor", despesa.getValor());
        data.put("descricao", despesa.getDescricao());
        data.put("categoria", despesa.getCategoria());
        data.put("data", despesa.getData());
        data.put("dataVencimento", despesa.getDataVencimento());

        firestore
                .collection("usuarios")
                .document(userId)
                .collection("despesas")
                .add(data);
    }
}
