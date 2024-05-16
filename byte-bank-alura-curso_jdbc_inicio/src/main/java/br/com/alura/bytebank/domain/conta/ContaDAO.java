package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

//A class ContaDAO é a responsavel por interagir com o banco de dados em sí
public class ContaDAO {

    private Connection conn;

    ContaDAO(Connection connection) {
        this.conn = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta) {
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO, cliente, true);

        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email, esta_ativa)" +
                "VALUES (?, ?, ?, ?, ?, ? )";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5, dadosDaConta.dadosCliente().email());
            preparedStatement.setBoolean(6,true);

            preparedStatement.execute();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> listar() throws SQLException {
        Set<Conta> contas = new HashSet<>();
        PreparedStatement ps;
        ResultSet rs;

        String sql = " SELECT * FROM conta WHERE  esta_ativa = true";
        try {
            ps = conn.prepareStatement(sql);
            //Oexecute query retorna um resultSet() é de fato uma linha de banco de dados(conteúdo)
            //Estamos pegando os itens do BD
            rs = ps.executeQuery();
            //A cada iteração eu pego uma linha e transformo num objeto, o while verifica se existe um proximo valor
            while (rs.next()) {
                Integer numero = rs.getInt(1);
                BigDecimal saldo = rs.getBigDecimal(2);
                String nome = rs.getString(3);
                String cpf = rs.getString(4);
                String email = rs.getString(5);
                Boolean estaAtiva = rs.getBoolean(6);

                DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);
                contas.add(new Conta(numero,saldo, cliente,estaAtiva));
            }
            rs.close();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contas;
    }
    public Conta listarPorNumero(Integer numero) {
        String sql = "SELECT * FROM conta WHERE numero = ? and esta_ativa = true";

        PreparedStatement ps;
        ResultSet rs;
        Conta conta = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, numero);
            rs = ps.executeQuery();

            while (rs.next()) {
                Integer numeroRecuperado = rs.getInt(1);
                BigDecimal saldo = rs.getBigDecimal(2);
                String nome = rs.getString(3);
                String cpf = rs.getString(4);
                String email = rs.getString(5);
                Boolean estaAtiva = rs.getBoolean(6);


                DadosCadastroCliente dadosCadastroCliente =
                        new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);

                conta = new Conta(numeroRecuperado, saldo, cliente,estaAtiva);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conta;
    }

    public void alterar(Integer numero, BigDecimal valor){
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";
        PreparedStatement ps;

        try {
            ps = conn.prepareStatement(sql);

            ps.setBigDecimal(1,valor);
            ps.setInt(2,numero);

            ps.execute();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public void deletar(Integer numeroDaConta){
        //Vale lembrar que a String sql vai no preparedStatement para executar a linha de comando
        //no Banco de dados assim realizando a funçao desejada
        String sql = "DELETE FROM conta WHERE numero = ? ";
        PreparedStatement ps;
        try{
            ps = conn.prepareStatement(sql);
            ps.setInt(1,numeroDaConta);

            ps.execute();
            ps.close();
            conn.close();
        }catch (SQLException e){
            throw new RuntimeException();
        }

    }
    public void alterarLogico(Integer numeroDaConta) {
        PreparedStatement ps;
        String sql = "UPDATE conta SET esta_ativa = false WHERE numero = ?";

        try {
            ps = conn.prepareStatement(sql);

            ps.setInt(1, numeroDaConta);

            ps.execute();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
