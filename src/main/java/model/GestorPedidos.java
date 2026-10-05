package model;
import java.util.ArrayList;

    public class GestorPedidos {

        private ArrayList<Pedido> pedidos;

        public GestorPedidos() {
            pedidos = new ArrayList<>();
        }

        public void agregarPedido(Pedido pedido) {
            pedidos.add(pedido);
        }

        public ArrayList<Pedido> getPedidos() {
            return pedidos;
        }
    }

