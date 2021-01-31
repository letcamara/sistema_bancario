package sistemaBancario.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UsuarioDTO {

	@NotBlank(message = "Cpf obrigatório")
	@Size(max = 11)
	public String cpf;

	@NotBlank(message = "Login obrigatório")
	@Size(max = 50)
	public String login;

	@NotBlank(message = "Nome obrigatório")
	@Size(max = 50)
	public String nome;

	@NotBlank(message = "Senha obrigatória")
	@Size(max = 20)
	public String senha;
	
	public UsuarioDTO() {}
	
	public UsuarioDTO(String cpf, String login, String nome, String senha) {
		this.cpf = cpf;
		this.login = login;
		this.nome = nome;
		this.senha = senha;
	}
	
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}

	
	
	
}
