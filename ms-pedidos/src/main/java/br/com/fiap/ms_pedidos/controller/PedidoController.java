package br.com.fiap.ms_pedidos.controller;

import br.com.fiap.ms_pedidos.dto.PedidoDTO;
import br.com.fiap.ms_pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PutMapping("/{pedidoID}/pagamento/confirmado")
    public void confirmarPagamento(@PathVariable Long pedidoID){

        pedidoService.confirmarPagamento(pedidoID);
    }

    @GetMapping("/port")
    public String port(@Value("${local.server.port}") String porta){

        return "Instância respondeu na porta" + porta;
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> getAll(){

        List<PedidoDTO> list = pedidoService.findAllPedidos();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> getOne(@PathVariable Long id){

        PedidoDTO pedidoDTO = pedidoService.findPedidoById(id);

        return ResponseEntity.ok(pedidoDTO);
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> savePedido(@RequestBody @Valid PedidoDTO pedidoDTO){

        pedidoDTO = pedidoService.savePedido(pedidoDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(pedidoDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(pedidoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> updatePedido(@PathVariable Long id,
                                                  @Valid @RequestBody PedidoDTO pedidoDTO){

        pedidoDTO = pedidoService.updatePedido(id, pedidoDTO);

        return ResponseEntity.ok(pedidoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id){

        pedidoService.deletePedidoById(id);

        return ResponseEntity.noContent().build();
    }
}
