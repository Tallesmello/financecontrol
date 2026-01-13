package br.com.talles.financecontrol;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import br.com.talles.financecontrol.model.Despesa;

/**
 * DAO = Data Access Object
 * Define O QUE pode ser feito no banco
 */
@Dao
public interface DespesaDao {

    // Inserir nova despesa
    @Insert
    void inserir(Despesa despesa);

    // Buscar todas as despesas
    @Query("SELECT * FROM despesas WHERE userId = :userId")
    List<Despesa> listarPorUsuario(String userId);

}

