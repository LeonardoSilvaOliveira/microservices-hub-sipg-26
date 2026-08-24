package br.com.fiap.ms_pagamentos.controller;

import br.com.fiap.ms_pagamentos.dto.PagamentoDTO;
import br.com.fiap.ms_pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizarPedido", fallbackMethod = "fallbackConfirmarPagamentoPendente")
        public ResponseEntity<PagamentoDTO> confirmarPagamentoDoPedido(@PathVariable @NotNull Long id){

        PagamentoDTO dto = pagamentoService.confirmarPagamentoDoPedido(id);

        return ResponseEntity.ok(dto);
    }
    public ResponseEntity<PagamentoDTO> fallbackconfirmarPagamentoDoPedido(Long id, Throwable e){

        log.error("Falha ao confirmar pedido {}. Ativando Erro: {}", id, e.getMessage());
        PagamentoDTO dto = pagamentoService.alterStatusPagamento(id);

        return ResponseEntity.status(503).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<PagamentoDTO>> getAllPagamentos(){

        List<PagamentoDTO> list = pagamentoService.findAllPagamentos();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDTO> getOne(@PathVariable Long id){

        PagamentoDTO pagamentoDTO = pagamentoService.findPagamentoById(id);

        return ResponseEntity.ok(pagamentoDTO);

    }

    @PostMapping
    public ResponseEntity<PagamentoDTO> createPagamento(@RequestBody @Valid PagamentoDTO pagamentoDTO){

        pagamentoDTO = pagamentoService.savePagamento(pagamentoDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(pagamentoDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(pagamentoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDTO> updatePagamento(@PathVariable Long id,
                                                        @Valid @RequestBody PagamentoDTO pagamentoDTO){

        pagamentoDTO = pagamentoService.updatePagamento(id, pagamentoDTO);

        return ResponseEntity.ok(pagamentoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePagamento(@PathVariable Long id){

        pagamentoService.deletePagamento(id);

        return ResponseEntity.noContent().build();
    }


}
