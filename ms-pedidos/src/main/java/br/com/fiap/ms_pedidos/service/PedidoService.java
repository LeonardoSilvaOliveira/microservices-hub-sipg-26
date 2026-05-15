package br.com.fiap.ms_pedidos.service;

import br.com.fiap.ms_pedidos.MsPedidosApplication;
import br.com.fiap.ms_pedidos.dto.ItemDoPedidoDTO;
import br.com.fiap.ms_pedidos.dto.PedidoDTO;
import br.com.fiap.ms_pedidos.entities.ItemDoPedido;
import br.com.fiap.ms_pedidos.entities.Pedido;
import br.com.fiap.ms_pedidos.entities.Status;
import br.com.fiap.ms_pedidos.exceptions.PedioPagoExpception;
import br.com.fiap.ms_pedidos.exceptions.ResourceNotFoundException;
import br.com.fiap.ms_pedidos.repositories.ItemDoPedidoRepository;
import br.com.fiap.ms_pedidos.repositories.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemDoPedidoRepository itemDoPedidoRepository;

    @Transactional
    public void confirmarPagamento(Long id){
        Optional<Pedido> pedido = pedidoRepository.findById(id);

        if (pedido.isEmpty()){
            throw new ResourceNotFoundException("Pedido não encontrado. ID:" + id);
        }
        pedido.get().setStatus(Status.PAGO);
        pedidoRepository.save(pedido.get());
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> findAllPedidos(){

        return pedidoRepository.findAll()
                .stream()
                .map(PedidoDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoDTO findPedidoById(Long id){

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        return new PedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO savePedido(PedidoDTO pedidoDTO){

        Pedido pedido = new Pedido();
        pedido.setData(LocalDate.now());
        pedido.setStatus(Status.CRIADO);
        mapDtpToPedido(pedidoDTO, pedido);
        pedido.calcularValorTotalDoPedido();
        pedido = pedidoRepository.save(pedido);
        return new PedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO updatePedido(Long id, PedidoDTO pedidoDTO){

        try {
            Pedido pedido = pedidoRepository.getReferenceById(id);
            if (pedido.getStatus().equals(Status.PAGO)){
                throw new PedioPagoExpception(
                        String.format("Pedido id: %d já está PAGO e não pode ser alterado", id));
            }
            pedido.getItens().clear();
            pedido.setData(LocalDate.now());
//            pedido.setStatus(Status.CRIADO);
            mapDtpToPedido(pedidoDTO, pedido);
            pedido.calcularValorTotalDoPedido();
            pedido = pedidoRepository.save(pedido);
            return new PedidoDTO(pedido);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }
    }

    @Transactional
    public void deletePedidoById(Long id){
        if(!pedidoRepository.existsById(id)){
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }

        pedidoRepository.deleteById(id);
    }

    private void mapDtpToPedido(PedidoDTO pedidoDTO, Pedido pedido) {

        pedido.setNome(pedidoDTO.getNome());
        pedido.setCpf(pedidoDTO.getCpf());

        for (ItemDoPedidoDTO itemDTO : pedidoDTO.getItens()){

            ItemDoPedido itemPedido = new ItemDoPedido();
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setDescricao(itemDTO.getDescricao());
            itemPedido.setPrecoUnitario(itemDTO.getPrecoUnitario());
            itemPedido.setPedido(pedido);
            pedido.getItens().add(itemPedido);
        }
    }
}
