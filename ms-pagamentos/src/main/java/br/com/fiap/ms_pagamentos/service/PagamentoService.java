package br.com.fiap.ms_pagamentos.service;

import br.com.fiap.ms_pagamentos.client.PedidoClient;
import br.com.fiap.ms_pagamentos.dto.PagamentoDTO;
import br.com.fiap.ms_pagamentos.entities.Pagamento;
import br.com.fiap.ms_pagamentos.entities.Status;
import br.com.fiap.ms_pagamentos.exceptions.ResourceNotFoundException;
import br.com.fiap.ms_pagamentos.exceptions.handler.PagamentoAprovadoException;
import br.com.fiap.ms_pagamentos.repository.PagamentoRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PedidoClient pagamentoClient;

    @Transactional
    public PagamentoDTO confirmarPagamentoDoPedido(Long id){
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pagamento não encontrado. ID:" +id)
        );

        pagamento.setStatus(Status.APROVADO);
        pagamentoRepository.save(pagamento);
        pagamentoClient.confirmarPagamento(pagamento.getPedidoId());
        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO confirmarpagamentoDOPedido(Long id){

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pagamento não encontrado. ID: " + id)
        );

        pagamento.setStatus(Status.APROVADO);
        pagamentoRepository.save(pagamento);

        try {
            pagamentoClient.confirmarPagamento(pagamento.getPedidoId());
        }catch (FeignException.NotFound e){
            throw new ResourceNotFoundException("Pedido não encontrado. ID: "+ pagamento.getPedidoId());
        }catch (FeignException e){
            throw new RuntimeException("Falha ao comunicar com ms-pedidos", e);
        }
        return new PagamentoDTO(pagamento);
    }

    @Transactional(readOnly = true)
    public List<PagamentoDTO> findAllPagamentos(){

        List<Pagamento> list = pagamentoRepository.findAll();

        return list.stream()
                .map(PagamentoDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public PagamentoDTO findPagamentoById(Long id){

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO savePagamento(PagamentoDTO pagamentoDTO){

        Pagamento pagamento = new Pagamento();

        mapDtoToPagamento(pagamentoDTO, pagamento);
        pagamento.setStatus(Status.CRIADO);
        pagamento = pagamentoRepository.save(pagamento);
        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO updatePagamento(Long id, PagamentoDTO pagamentoDTO){

        try {
            Pagamento pagamento = pagamentoRepository.getReferenceById(id);

            if(pagamento.getStatus().equals(Status.APROVADO)){
                throw new PagamentoAprovadoException(
                        String.format("Pagamento id %d já está APROVADO e não pode ser alterado", id)
                );
            }
            mapDtoToPagamento(pagamentoDTO, pagamento);
            pagamento.setStatus(pagamentoDTO.getStatus());
            pagamento = pagamentoRepository.save(pagamento);
            return new PagamentoDTO(pagamento);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }
    }

    public void deletePagamento(Long id){

        if(!pagamentoRepository.existsById(id)){

            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }

        pagamentoRepository.deleteById(id);
    }

    private void mapDtoToPagamento(PagamentoDTO pagamentoDTO, Pagamento pagamento) {

        pagamento.setValor(pagamentoDTO.getValor());
        pagamento.setNome(pagamentoDTO.getNome());
        pagamento.setNumeroCartao(pagamentoDTO.getNumeroCartao());
        pagamento.setValidade(pagamentoDTO.getValidade());
        pagamento.setCodigoSeguranca(pagamentoDTO.getCodigoSeguranca());
        pagamento.setPedidoId(pagamentoDTO.getPedidoId());
    }
}
