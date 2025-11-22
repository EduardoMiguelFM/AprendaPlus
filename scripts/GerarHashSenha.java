// Script Java para gerar hash BCrypt de uma senha
// Execute este código para gerar o hash correto

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GerarHashSenha {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senha = "admin123";
        String hash = encoder.encode(senha);
        System.out.println("Senha: " + senha);
        System.out.println("Hash: " + hash);
        
        // Verificar se o hash corresponde à senha
        boolean matches = encoder.matches(senha, hash);
        System.out.println("Hash válido: " + matches);
    }
}

