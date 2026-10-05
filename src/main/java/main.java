
import model.GestorPedidos;
import vista.VentanaPrincipal;

public class main {

    public static void main(String[] args){
        GestorPedidos gestorPedidos = new GestorPedidos();

        VentanaPrincipal ventanaPrincipal = new VentanaPrincipal(gestorPedidos);

        ventanaPrincipal.setVisible(true);

    }
}
