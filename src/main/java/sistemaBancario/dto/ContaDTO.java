package sistemaBancario.dto;

import sistemaBancario.enums.Sigla;
import sistemaBancario.models.Conta;

public class ContaDTO {
	private Long id;
    private String nome;
    private Double saldo;
    private Sigla sigla;
    private String login;

    public ContaDTO(Conta conta) {
        this.setNome(conta.getNome());
        this.setSaldo(conta.getSaldo());
    }

    public ContaDTO(){}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getSaldo() {
		return saldo;
	}

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}

	public Sigla getSigla() {
		return sigla;
	}

	public void setSigla(Sigla sigla) {
		this.sigla = sigla;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}
}
