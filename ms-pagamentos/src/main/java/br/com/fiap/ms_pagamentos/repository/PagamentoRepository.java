package br.com.fiap.ms_pagamentos.repository;

import br.com.fiap.ms_pagamentos.entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository  extends JpaRepository<Pagamento, Long> {

}
