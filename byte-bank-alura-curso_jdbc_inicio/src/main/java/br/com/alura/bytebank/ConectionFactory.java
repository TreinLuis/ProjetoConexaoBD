package br.com.alura.bytebank;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectionFactory {
    public HikariDataSource createDataSource(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/byte_bank");
        config.setUsername("root");
        config.setPassword("Luli4895");
        config.setMaximumPoolSize(10);//Quantas conexoes que eu quero que fiquem abertas para serem utilizadas

        return new HikariDataSource(config);
    }
    public Connection recuperarConexao(){
        //Precisamos passar uma String de identificação para fazermos s integração
        try {
            //chamamos o metodo que faz a conexao em si
            return createDataSource().getConnection();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
