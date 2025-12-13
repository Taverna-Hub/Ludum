package org.ludum.aplicacao.oficina.mod.decorator;

import java.util.List;

import org.ludum.aplicacao.oficina.mod.ModResumo;
import org.ludum.aplicacao.oficina.mod.ModServicoAplicacao;
import org.ludum.aplicacao.oficina.mod.porta.ModMalwareScanner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ScanSegurancaDecorator implements ModServicoAplicacao {

    private final ModServicoAplicacao decorado;
    private final ModMalwareScanner scanner;

    public ScanSegurancaDecorator(
        @Qualifier("modServicoAplicacaoBase") ModServicoAplicacao decorado, 
        ModMalwareScanner scanner
    ) {
        this.decorado = decorado;
        this.scanner = scanner;
    }

    @Override
    public void publicarNovoMod(String jogoId, String autorId, String nome, String descricao, String notas, byte[] arquivo) {
        validarArquivo(arquivo);
        decorado.publicarNovoMod(jogoId, autorId, nome, descricao, notas, arquivo);
    }

    @Override
    public void lancarNovaVersao(String modId, String autorId, String notas, byte[] arquivo) {
        validarArquivo(arquivo);
        decorado.lancarNovaVersao(modId, autorId, notas, arquivo);
    }

    // --- Métodos "Pass-Through" (Apenas repassa) ---
    // O decorator é transparente: o que ele não altera, ele entrega exatamente como recebeu.

    @Override
    public void atualizarDetalhes(String modId, String autorId, String novoNome, String novaDescricao) {
        decorado.atualizarDetalhes(modId, autorId, novoNome, novaDescricao);
    }

    @Override
    public void removerMod(String modId, String autorId) {
        decorado.removerMod(modId, autorId);
    }

    @Override
    public List<ModResumo> pesquisarResumosPorJogo(String jogoId) {
        return decorado.pesquisarResumosPorJogo(jogoId);
    }

    @Override
    public ModResumo buscarPorId(String modId) {
        return decorado.buscarPorId(modId);
    }

    // Método auxiliar privado
    private void validarArquivo(byte[] arquivo) {
        if (arquivo != null && scanner.contemMalware(arquivo, "upload-usuario")) {
            throw new SecurityException("Malware detectado! Operação bloqueada.");
        }
    }
}