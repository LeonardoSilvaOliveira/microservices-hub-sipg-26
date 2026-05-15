package br.com.fiap.ms_pedidos.exceptions;

public class PedioPagoExpception extends RuntimeException {
    public PedioPagoExpception(String message) {
        super(message);
    }
}
