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
        String sql="DELETE FROM candidatura WHERE id=?";
        try(Connection conn=ConexionDb.obtenerConexion();
            PreparedStatement pstm= conn.prepareStatement(sql)){

            pstm.setLong(1,id);
            return pstm.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
