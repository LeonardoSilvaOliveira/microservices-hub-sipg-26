package br.com.fiap.ms_pedidos.exceptions;

public class PedidoPagoException extends RuntimeException {
    public PedidoPagoException(String message) {
        super(message);
    }
}
