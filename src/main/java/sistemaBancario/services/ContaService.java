package sistemaBancario.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sistemaBancario.dto.ExtratoDTO;
import sistemaBancario.dto.LancamentoDTO;
import sistemaBancario.enums.Sigla;
import sistemaBancario.models.Conta;
import sistemaBancario.models.Lancamento;
import sistemaBancario.models.PlanoConta;
import sistemaBancario.repository.ContaRepository;

@Service
public class ContaService {

	@Autowired
	private ContaRepository repository;

	@Autowired
	private LancamentoService lancamentoService;

	@Autowired
	private PlanoContaService planoContaService;

	@Transactional
	public String depositar(LancamentoDTO lancamentoDTO) {
		if (lancamentoDTO.getValor() < 0)
			throw new ArithmeticException("Não é possível depositar um valor negativo");

		Conta conta = buscar(lancamentoDTO.getContaOrigem().login, lancamentoDTO.getContaOrigem().sigla);

		PlanoConta planoConta = planoContaService.buscar(lancamentoDTO.getPlanoConta(),
				lancamentoDTO.getContaOrigem().login);

		conta.setSaldo(conta.getSaldo() + lancamentoDTO.getValor());

		Lancamento lancamento = new Lancamento(conta, lancamentoDTO.getValor(), conta, planoConta,
				lancamentoDTO.getDescricao());
		lancamentoService.realizarLancamento(lancamento);
		repository.save(conta);
		return "Deposito realizado com Sucesso";

	}

	@Transactional
	public String pagar(LancamentoDTO lancamentoDTO) {
		if (lancamentoDTO.getValor() < 0)
			throw new ArithmeticException("Não é possível realizar pagamento de um valor negativo");
		Conta conta = buscar(lancamentoDTO.getContaOrigem().login, lancamentoDTO.getContaOrigem().sigla);

		PlanoConta planoConta = planoContaService.buscar(lancamentoDTO.getPlanoConta(),
				lancamentoDTO.getContaOrigem().login);

		if (conta.getSaldo() < lancamentoDTO.getValor())
			throw new ArithmeticException("Saldo Insuficiente para realizar pagamento");

		lancamentoDTO.setValor(lancamentoDTO.getValor() * (-1));

		conta.setSaldo(conta.getSaldo() + lancamentoDTO.getValor());

		Lancamento lancamento = new Lancamento(conta, lancamentoDTO.getValor(), conta, planoConta,
				lancamentoDTO.getDescricao());
		lancamentoService.realizarLancamento(lancamento);
		repository.save(conta);
		return "Pagamento realizado com Sucesso";

	}

	@Transactional
	public String transferir(LancamentoDTO lancamentoDTO) {
		if (lancamentoDTO.getValor() < 0)
			throw new ArithmeticException("Não é possível realizar transferencia de um valor negativo");

		Conta origem = buscar(lancamentoDTO.getContaOrigem().login, lancamentoDTO.getContaOrigem().sigla);
		Conta destino = buscar(lancamentoDTO.getContaDestino().login, lancamentoDTO.getContaDestino().sigla);

		if (origem.getSaldo() < lancamentoDTO.getValor())
			throw new ArithmeticException("Saldo Insuficiente para realizar transferencia");

		PlanoConta planoConta = planoContaService.buscar(lancamentoDTO.getPlanoConta(),
				lancamentoDTO.getContaOrigem().login);

		Lancamento lancamento;
		// Registro da saída da conta de origem
		destino.setSaldo(origem.getSaldo() + lancamentoDTO.getValor());
		lancamento = new Lancamento(destino, lancamentoDTO.getValor(), origem, planoConta,
				lancamentoDTO.getDescricao());
		lancamentoService.realizarLancamento(lancamento);

		// Registro da entada da conta de destino
		lancamentoDTO.setValor(lancamentoDTO.getValor() * (-1));
		origem.setSaldo(origem.getSaldo() + lancamentoDTO.getValor());
		lancamento = new Lancamento(origem, lancamentoDTO.getValor(), destino, planoConta,
				lancamentoDTO.getDescricao());
		lancamentoService.realizarLancamento(lancamento);

		// Atualização das Contas
		repository.save(origem);
		repository.save(destino);
		return String.format("Transferencia de R$%.2f de %s para %s realizada com Sucesso",
				(lancamentoDTO.getValor() * (-1)), lancamentoDTO.getContaOrigem().login,
				lancamentoDTO.getContaDestino().login);
	}

	public ExtratoDTO consultarExtrato(String login, Sigla sigla, LocalDate dataInicio, LocalDate dataFim) {
		Conta conta = buscar(login, sigla);
		ArrayList<LancamentoDTO> lancamentos = lancamentoService.getLancamentos(conta.getId(), dataInicio, dataFim);

		return new ExtratoDTO(conta.getSaldo(), lancamentos);
	}

	public ExtratoDTO consultarExtrato(String login, Sigla sigla) {
		Conta conta = buscar(login, sigla);
		ArrayList<LancamentoDTO> lancamentos = lancamentoService.getLancamentos(conta.getId());

		return new ExtratoDTO(conta.getSaldo(), lancamentos);
	}

	public Double consultarSaldo(Long id) {
		Conta conta = buscar(id);
		return conta.getSaldo();
	}

	public Conta buscar(String login, Sigla sigla) {
		try {
			Conta conta = repository.findByTitularLoginAndSigla(login, sigla);
			return conta;
		}catch (Exception e) {
			throw new NullPointerException(String.format("Usário %s não existe",login));
		}
	}

	public Conta buscar(Long id) {
		Optional<Conta> optconta;
		optconta = repository.findById(id);
		return optconta.get();
	}

	public ArrayList<Conta> buscar(String login) {
		return repository.findAllByTitularLogin(login);
	}

}
