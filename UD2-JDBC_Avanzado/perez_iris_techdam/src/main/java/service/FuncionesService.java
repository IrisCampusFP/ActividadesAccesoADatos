package service;

import java.sql.Connection;
import config.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

public class FuncionesService {

    public void calcularSumaSalarios(String departamento) {

        // Llamada a la función SQL para obtener la suma de salarios
        try (Connection con = DatabaseConfig.getConnection();
             CallableStatement cstmt = con.prepareCall("{? = call suma_salarios_departamento(?)}")) {

            // Se establecen los parámetros de entrada (departamento)
            cstmt.setString(2, departamento);

            // Se registra el parámetro de salida (suma de salarios)
            cstmt.registerOutParameter(1, Types.DECIMAL);

            // Ejecución de la llamada a la función
            cstmt.execute();

            // Se obtiene el valor de la suma de salarios
            BigDecimal sumaSalarios = cstmt.getBigDecimal(1);

            System.out.println("La suma total de salarios del departamento de '" + departamento + "' es: " + sumaSalarios);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al calcular la suma de salarios.");
        }
    }
}
