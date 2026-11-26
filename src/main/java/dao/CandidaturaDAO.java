package dao;

import ConfigDB.ConexionDb;
import model.Candidatura;
import model.EstadoCandidatura;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CandidaturaDAO {

    public boolean modificar(Candidatura candidatura) {
        String sql = "UPDATE candidaturas SET titulo=?, empresa=?, estado=?, fecha_postulacion=?, enlace=?, notas=? WHERE id=?";
        try (Connection conn = ConexionDb.obtenerConexion();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, candidatura.titulo());
            pstm.setString(2, candidatura.empresa());
            pstm.setString(3, candidatura.estadoCandidatura().name());
            pstm.setDate(4, java.sql.Date.valueOf(candidatura.fechaPostulacion()));
            pstm.setString(5, candidatura.enlace());
            pstm.setString(6, candidatura.notas());
            pstm.setLong(7, candidatura.id());

            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //METODO PARA MOSTRAR TODAS LAS CANDIDATURAS
    public List<Candidatura> leerTodas(){
        List<Candidatura> lista=new ArrayList<>();
        String sql="SELECT id,titulo,empresa,estado_candidatura,fecha_postulacion,enlace,notas FROM candidaturas";
        try (Connection conn= ConexionDb.obtenerConexion();
             PreparedStatement pstm= conn.prepareStatement(sql);
             ResultSet rs=pstm.executeQuery()){
            while (rs.next()){
                lista.add(mapearFila(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //METODO PARA ANHADIR CANDIDATURA
    public void anhadir(Candidatura candidatura){
        String sql="INSERT INTO candidaturas (titulo,empresa,estado_candidatura,fecha_postulacion,enlace,notas) VALUES(?, ?, ?, ?, ?, ?)";
        try(Connection conn=ConexionDb.obtenerConexion();
            PreparedStatement pstm= conn.prepareStatement(sql);){
            pstm.setString(1,candidatura.titulo());
            pstm.setString(2,candidatura.empresa());
            pstm.setString(3,candidatura.estadoCandidatura().name());
            pstm.setDate(4, Date.valueOf(candidatura.fechaPostulacion()));
            pstm.setString(5,candidatura.enlace());
            pstm.setString(6,candidatura.notas());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //METODO PARA BUSCAR POR ID EN CANDIDATURA
    public Candidatura buscarPorId(long id){
        String sql="SELECT id,titulo,empresa,estado_candidatura,fecha_postulacion,enlace,notas FROM candidaturas WHERE id= ?";
        try(Connection conn=ConexionDb.obtenerConexion();
            PreparedStatement pstm=conn.prepareStatement(sql)) {
            pstm.setLong(1,id);
            try(ResultSet rs=pstm.executeQuery()){
                if (rs.next())
                    return mapearFila(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    //METODO PARA ELIMINAR CANDIDATURA
    public boolean eliminar(Long id){
        String sql="DELETE FROM candidaturas WHERE id=?";
        try(Connection conn=ConexionDb.obtenerConexion();
            PreparedStatement pstm= conn.prepareStatement(sql)){

            pstm.setLong(1,id);
            return pstm.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public java.util.List<model.Candidatura> importar(java.nio.file.Path rutaCsv) throws java.io.IOException, java.sql.SQLException {
        java.time.format.DateTimeFormatter formatoFecha =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu")
                        .withResolverStyle(java.time.format.ResolverStyle.STRICT);

        java.util.List<model.Candidatura> importadas = new java.util.ArrayList<>();
        java.util.List<String> lineas = java.nio.file.Files.readAllLines(rutaCsv, java.nio.charset.StandardCharsets.UTF_8);

        if (lineas.isEmpty()) {
            throw new IllegalArgumentException("El archivo CSV está vacío.");
        }

        String cabecera = lineas.get(0).replace("\uFEFF", "").trim();
        if (!cabecera.equalsIgnoreCase("id;titulo;empresa;estado;fechaPostulacion;enlace;notas")) {
            throw new IllegalArgumentException("Cabecera incorrecta. Se esperaba: id;titulo;empresa;estado;fechaPostulacion;enlace;notas");
        }

        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (linea.isEmpty()) continue;

            String[] campos = linea.split(";", -1);
            if (campos.length != 7) {
                throw new IllegalArgumentException("Error en la línea " + (i + 1) + ": debe tener 7 columnas separadas por ';'.");
            }

            String titulo = campos[1].trim();
            String empresa = campos[2].trim();
            if (titulo.isEmpty() || empresa.isEmpty()) {
                throw new IllegalArgumentException("Error en la línea " + (i + 1) + ": título y empresa son obligatorios.");
            }

            model.EstadoCandidatura estado;
            try {
                estado = model.EstadoCandidatura.valueOf(campos[3].trim().toUpperCase(java.util.Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error en la línea " + (i + 1) + ": estado '" + campos[3] + "' no válido.");
            }

            java.time.LocalDate fecha;
            try {
                fecha = java.time.LocalDate.parse(campos[4].trim(), formatoFecha);
            } catch (java.time.format.DateTimeParseException e) {
                throw new IllegalArgumentException("Error en la línea " + (i + 1) + ": fecha '" + campos[4] + "' no válida (usa dd/MM/yyyy).");
            }

            String enlace = campos[5].trim();
            String notas = campos[6].trim();

            model.Candidatura nueva = new model.Candidatura(0L, titulo, empresa, estado, fecha, enlace, notas);
            anhadir(nueva);
            importadas.add(nueva);
        }

        return importadas;
    }
    //METODO PARA PASAR LAS CANDIDATURA A OBJETO
    private Candidatura mapearFila(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String titulo = rs.getString("titulo");
        String empresa = rs.getString("empresa");
        EstadoCandidatura estado = EstadoCandidatura.valueOf(
                rs.getString("estado_candidatura").trim().toUpperCase(Locale.ROOT)
        );
        LocalDate fecha = rs.getDate("fecha_postulacion").toLocalDate();
        String enlace = rs.getString("enlace");
        String notas = rs.getString("notas");

        return new Candidatura(id, titulo, empresa, estado, fecha, enlace, notas);
    }

}
