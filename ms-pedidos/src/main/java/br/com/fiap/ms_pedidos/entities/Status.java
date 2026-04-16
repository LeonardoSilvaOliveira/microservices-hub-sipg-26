package br.com.fiap.ms_pedidos.entities;

public enum Status {

    CRIADO,
    PAGAMENTO_PENDENTE,
    PAGO,
    CONFIRMADO,
    FALHA_PAGAMENTO,
    SAIU_PARA_ENTREGA,
    ENTREGUE,
    CANCELADO
}
