package br.com.talles.financecontrol.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entidade do banco de dados
 * Cada objeto Despesa = uma linha da tabela
 */
@Entity(tableName = "despesas")
public class Despesa {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double valor;
    private String descricao;

    //categoria da despesa
    private String categoria;
    private Long data; // data da despesa em timestamp
    private Long dataVencimento; // opcional
    private String userId;


    public Despesa(double valor, String descricao, String categoria, Long data, Long dataVencimento, String userId) {
        this.valor = valor;
        this.descricao = descricao;
        this.categoria = categoria;
        this.data = data;
        this.dataVencimento = dataVencimento;
        this.userId = userId;
    }

    // GETTERS Room
    public int getId() {
        return id;
    }

    public double getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getUserId() {
        return userId;
    }

    // SETTER do ID
    public void setId(int id) {
        this.id = id;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public Long getDataVencimento() {
        return dataVencimento;
    }


}



