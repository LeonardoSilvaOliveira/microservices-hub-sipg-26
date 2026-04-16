package br.com.fiap.ms_pagamentos.dto;

import br.com.fiap.ms_pagamentos.entities.Pagamento;
import br.com.fiap.ms_pagamentos.entities.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PagamentoDTO {

    private Long id;

    @NotNull(message = "O campo valor é requerido")
    @Positive(message = "O campo valor deve ser um número posito")
    private BigDecimal valor;

    @NotBlank(message = "O campo nome é requerido")
    @Size(min = 3, max = 50, message = "O campo nome deve ter entre 3 e 50 caracteres")
    private String nome;

    @NotBlank(message = "O campo número do cartão é requerido")
    @Size(min = 16, max = 16, message = "O campo número do cartão deve ter 16 caracteres")
    private String numeroCartao;

    @NotBlank(message = "O campo validade é requerido")
    @Size(min = 5, max = 5, message = "O campo validade deve ter 5 caracteres")
    private String validade;

    @NotBlank(message = "O campo código de segurança é requerido")
    @Size(min = 3, max = 3, message = "O campo código de segurança deve ter 3 caracteres")
    private String codigoSeguranca;

    private Status status;

    @NotNull(message = "O campo pedido id é obritaório")
    private Long pedidoId;

    public PagamentoDTO(Pagamento pagamento) {
        id = pagamento.getId();
        valor = pagamento.getValor();
        nome = pagamento.getNome();
        numeroCartao = pagamento.getNumeroCartao();
        validade = pagamento.getValidade();
        codigoSeguranca = pagamento.getCodigoSeguranca();
        status = pagamento.getStatus();
        pedidoId = pagamento.getPedidoId();
    }
}
