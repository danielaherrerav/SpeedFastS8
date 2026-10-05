package model;

public class Pedido {

    private int id;
    private String direccionEntrega;
    private String tipo;
    private String repartidor;

    public Pedido(int id, String direccionEntrega, String tipo) {
        this.id = id;
        this.direccionEntrega = direccionEntrega;
        this.tipo = tipo;
        this.repartidor = "Sin Asignar";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = repartidor;
    }

    @Override
    public String toString() {
        return "Pedido #" + id
                + " - " + direccionEntrega
                + " - " + tipo
                + " - Repartidor: " + repartidor;
    }
}

