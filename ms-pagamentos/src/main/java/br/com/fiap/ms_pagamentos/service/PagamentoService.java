package br.com.fiap.ms_pagamentos.service;

import br.com.fiap.ms_pagamentos.dto.PagamentoDTO;
import br.com.fiap.ms_pagamentos.entities.Pagamento;
import br.com.fiap.ms_pagamentos.entities.Status;
import br.com.fiap.ms_pagamentos.exceptions.ResourceNotFoundException;
import br.com.fiap.ms_pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

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
