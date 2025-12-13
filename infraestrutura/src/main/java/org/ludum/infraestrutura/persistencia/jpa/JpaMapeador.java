package org.ludum.infraestrutura.persistencia.jpa;

import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.financeiro.carteira.entidades.Saldo;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.comunidade.review.entidades.Review;
import org.ludum.dominio.comunidade.review.entidades.ReviewId;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.Versao;
import org.ludum.dominio.catalogo.jogo.entidades.VersaoId;
import org.ludum.dominio.catalogo.jogo.entidades.PacoteZip;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.ludum.dominio.comunidade.post.entidades.Post;
import org.ludum.dominio.comunidade.post.entidades.PostId;
import org.ludum.dominio.comunidade.post.entidades.Comentario;
import org.ludum.dominio.comunidade.post.entidades.ComentarioId;
import org.ludum.dominio.identidade.seguimento.entities.Seguimento;
import org.ludum.dominio.identidade.seguimento.entities.SeguimentoId;
import org.ludum.infraestrutura.config.AsaasConfig;
import org.ludum.dominio.identidade.seguimento.entities.AlvoId;
import org.ludum.dominio.catalogo.biblioteca.entidades.Biblioteca;
import org.ludum.dominio.catalogo.biblioteca.entidades.ItemBiblioteca;
import org.ludum.dominio.catalogo.biblioteca.estruturas.IteratorBiblioteca; 

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.ludum.dominio.oficina.mod.entidades.Mod;
import org.ludum.dominio.oficina.mod.entidades.VersaoMod;
import org.ludum.dominio.oficina.mod.enums.StatusMod;

@Component
public class JpaMapeador extends ModelMapper {

  JpaMapeador() {
    var configuracao = getConfiguration();
    configuracao.setFieldMatchingEnabled(true);
    configuracao.setFieldAccessLevel(AccessLevel.PRIVATE);

    addConverter(new AbstractConverter<CarteiraJpa, Carteira>() {
      @Override
      protected Carteira convert(CarteiraJpa source) {
        var id = new ContaId(source.id);
        Saldo saldo = new Saldo(source.disponivel, source.bloqueado);
        var carteira = new Carteira(id, saldo);
        carteira.setContaExternaValida(source.contaExternaValida);
        carteira.setContaExterna(source.contaExterna);

        return carteira;
      }
    });

    addConverter(new AbstractConverter<Carteira, CarteiraJpa>() {
      @Override
      protected CarteiraJpa convert(Carteira source) {
        CarteiraJpa jpa = new CarteiraJpa();
        jpa.id = source.getId().getValue();
        jpa.disponivel = source.getSaldo().getDisponivel();
        jpa.bloqueado = source.getSaldo().getBloqueado();
        jpa.contaExternaValida = source.isContaExternaValida();
        jpa.contaExterna = source.getContaExterna();
        return jpa;
      }
    });

    addConverter(new AbstractConverter<ContaJpa, Conta>() {
      @Override
      protected Conta convert(ContaJpa source) {
        return new Conta(
            new ContaId(source.id),
            source.nome,
            source.senhaHash,
            source.tipo,
            source.status);
      }
    });

    addConverter(new AbstractConverter<Conta, ContaJpa>() {
      @Override
      protected ContaJpa convert(Conta source) {
        ContaJpa jpa = new ContaJpa();
        jpa.id = source.getId().getValue();
        jpa.nome = source.getNome();
        jpa.senhaHash = source.getSenhaHash();
        jpa.tipo = source.getTipo();
        jpa.status = source.getStatus();
        return jpa;
      }
    });

    addConverter(new AbstractConverter<ReviewJpa, Review>() {
      @Override
      protected Review convert(ReviewJpa source) {
        var id = new ReviewId(source.id);
        var jogoId = new JogoId(source.jogoId);
        var autorId = new ContaId(source.autorId);
        return new Review(id, jogoId, autorId, source.nota, source.titulo, source.texto, source.data,
            source.isRecomendado, source.status);
      }
    });

    addConverter(new AbstractConverter<Review, ReviewJpa>() {
      @Override
      protected ReviewJpa convert(Review source) {
        var jpa = new ReviewJpa();
        jpa.id = source.getId().getValue();
        jpa.jogoId = source.getJogoId().getValue();
        jpa.autorId = source.getAutorId().getValue();
        jpa.nota = source.getNota();
        jpa.titulo = source.getTitulo();
        jpa.texto = source.getTexto();
        jpa.data = source.getData();
        jpa.dataUltimaEdicao = source.getDataUltimaEdicao();
        jpa.isRecomendado = source.isRecomendado();
        jpa.status = source.getStatus();
        return jpa;
      }
    });

    addConverter(new AbstractConverter<SeguimentoJpa, Seguimento>() {
      @Override
      protected Seguimento convert(SeguimentoJpa source) {
        var id = new SeguimentoId(source.id);
        var seguidorId = new ContaId(source.seguidorId);
        var seguidoId = new AlvoId(source.seguidoId);
        return new Seguimento(id, seguidorId, seguidoId, source.tipoAlvo, source.dataSeguimento);
      }
    });

    addConverter(new AbstractConverter<Seguimento, SeguimentoJpa>() {
      @Override
      protected SeguimentoJpa convert(Seguimento source) {
        var jpa = new SeguimentoJpa();
        jpa.id = source.getId().getValue();
        jpa.seguidorId = source.getSeguidorId().getValue();
        jpa.seguidoId = source.getSeguidoId().getValue();
        jpa.tipoAlvo = source.getTipoAlvo();
        jpa.dataSeguimento = source.getDataSeguimento();
        return jpa;
      }
    });

    // TODO: Adicionar quando Conta estiver funcionando
    // addConverter(new AbstractConverter<TransacaoJpa, Transacao>() {
    // @Override
    // protected Transacao convert(TransacaoJpa source) {
    // var id = map(source.id, TransacaoId.class);
    // var transacao = new Transacao(source.id, source.);

    // }
    // });

    addConverter(new AbstractConverter<TagJpa, Tag>() {
      @Override
      protected Tag convert(TagJpa source) {
        return new Tag(
            new TagId(source.id),
            source.nome);
      }
    });

    addConverter(new AbstractConverter<Tag, TagJpa>() {
      @Override
      protected TagJpa convert(Tag source) {
        TagJpa jpa = new TagJpa();
        jpa.id = source.getId().getValue();
        jpa.nome = source.getNome();
        return jpa;
      }
    });

    addConverter(new AbstractConverter<JogoJpa, Jogo>() {
      @Override
      protected Jogo convert(JogoJpa source) {
        try {
          List<Tag> tags = source.tagIds != null ? source.tagIds.stream()
              .map(tagId -> new Tag(new TagId(tagId), "Tag-" + tagId.substring(0, Math.min(5, tagId.length()))))
              .collect(Collectors.toList()) : new ArrayList<>();

          URL capaOficial = source.capaOficial != null && !source.capaOficial.isEmpty() 
              ? URI.create(source.capaOficial).toURL() : null;

          Jogo jogo = new Jogo(
              new JogoId(source.id),
              new ContaId(source.desenvolvedoraId),
              source.titulo,
              source.descricao,
              capaOficial,
              tags,
              source.isNSFW,
              source.dataDeLancamento);

          if (source.screenshots != null) {
            for (String screenshotUrl : source.screenshots) {
              if (screenshotUrl != null && !screenshotUrl.isEmpty()) {
                jogo.adicionarScreenshot(URI.create(screenshotUrl).toURL());
              }
            }
          }

          if (source.videos != null) {
            for (String videoUrl : source.videos) {
              if (videoUrl != null && !videoUrl.isEmpty()) {
                jogo.adicionarVideo(URI.create(videoUrl).toURL());
              }
            }
          }

          // Define o status diretamente via reflection para evitar validações
          // que já foram feitas quando o jogo foi originalmente publicado
          if (source.status != null && source.status != jogo.getStatus()) {
            var statusField = Jogo.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(jogo, source.status);
          }

          if (source.versoes != null && !source.versoes.isEmpty()) {
            var versaoHistoryField = Jogo.class.getDeclaredField("versaoHistory");
            versaoHistoryField.setAccessible(true);
            @SuppressWarnings("unchecked")
            var listaVersoes = (List<Versao>) versaoHistoryField.get(jogo);

            for (VersaoJpa vJpa : source.versoes) {
              PacoteZip pacote = new PacoteZip(vJpa.conteudo);

              Versao versao = new Versao(
                  pacote,
                  new JogoId(source.id),
                  new VersaoId(vJpa.id),
                  vJpa.nomeVersao,
                  vJpa.descricaoVersao,
                  vJpa.dataUpload);

              listaVersoes.add(versao);
            }
          }

          return jogo;
        } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException("Erro ao converter JogoJpa para Jogo: " + e.getMessage(), e);
        }
      }
    });

    addConverter(new AbstractConverter<Jogo, JogoJpa>() {
      @Override
      protected JogoJpa convert(Jogo source) {
        JogoJpa jpa = new JogoJpa();
        jpa.id = source.getId().getValue();
        jpa.desenvolvedoraId = source.getDesenvolvedoraId().getValue();
        jpa.slug = source.getSlug().getValor();
        jpa.capaOficial = source.getCapaOficial() != null ? source.getCapaOficial().toString() : null;
        jpa.status = source.getStatus();
        jpa.titulo = source.getTitulo();
        jpa.descricao = source.getDescricao();
        jpa.screenshots = source.getScreenshots().stream()
            .map(URL::toString)
            .collect(Collectors.toList());
        jpa.videos = source.getVideos().stream()
            .map(URL::toString)
            .collect(Collectors.toList());
        jpa.tagIds = source.getTags().stream()
            .map(tag -> tag.getId().getValue())
            .collect(Collectors.toList());
        jpa.isNSFW = source.isNSFW();
        jpa.dataDeLancamento = source.getDataDeLancamento();

        if (source.getVersaoHistory() != null) {
          jpa.versoes = source.getVersaoHistory().stream()
              .map(versao -> {
                VersaoJpa vJpa = new VersaoJpa();
                vJpa.id = versao.getId().getValue();
                vJpa.jogo = jpa;
                vJpa.conteudo = versao.getPacoteZip().getConteudo();
                vJpa.nomeVersao = versao.getNomeVersao();
                vJpa.descricaoVersao = versao.getDescricaoVersao();
                vJpa.dataUpload = versao.getDataUpload();
                return vJpa;
              })
              .collect(Collectors.toList());
        }

        return jpa;
      }
    });

    addConverter(new AbstractConverter<PostJpa, Post>() {
      @Override
      protected Post convert(PostJpa source) {
        try {
          List<Tag> tags = source.tagIds.stream()
              .map(tagId -> new Tag(new TagId(tagId), "Tag-" + tagId.substring(0, Math.min(5, tagId.length()))))
              .collect(Collectors.toList());

          URL imagem = source.imagem != null ? URI.create(source.imagem).toURL() : null;

          Post post = new Post(
              new PostId(source.id),
              new JogoId(source.jogoId),
              new ContaId(source.autorId),
              source.titulo,
              source.conteudo,
              source.dataPublicacao,
              imagem,
              source.status,
              tags);

          post.setDataAgendamento(source.dataAgendamento);

          for (ComentarioJpa comentarioJpa : source.comentarios) {
            Comentario comentario = new Comentario(
                new ComentarioId(comentarioJpa.id),
                new PostId(comentarioJpa.postId),
                new ContaId(comentarioJpa.autorId),
                comentarioJpa.texto,
                comentarioJpa.data);
            if (comentarioJpa.oculto) {
              comentario.ocultar();
            }
            post.adicionarComentario(comentario);
          }

          for (CurtidaJpa curtidaJpa : source.curtidas) {
            post.adicionarCurtida(new ContaId(curtidaJpa.contaId));
          }

          return post;
        } catch (Exception e) {
          throw new RuntimeException("Erro ao converter PostJpa para Post", e);
        }
      }
    });

    addConverter(new AbstractConverter<Post, PostJpa>() {
      @Override
      protected PostJpa convert(Post source) {
        PostJpa jpa = new PostJpa();
        jpa.id = source.getId().getId();
        jpa.jogoId = source.getJogoId().getValue();
        jpa.autorId = source.getAutorId().getValue();
        jpa.titulo = source.getTitulo();
        jpa.conteudo = source.getConteudo();
        jpa.dataPublicacao = source.getDataPublicacao();
        jpa.dataAgendamento = source.getDataAgendamento();
        jpa.imagem = source.getImagem() != null ? source.getImagem().toString() : null;
        jpa.status = source.getStatus();

        jpa.tagIds = source.getTags().stream()
            .map(tag -> tag.getId().getValue())
            .collect(Collectors.toList());

        jpa.comentarios = source.getComentarios().stream()
            .map(comentario -> {
              ComentarioJpa cJpa = new ComentarioJpa();
              cJpa.id = comentario.getId().getId();
              cJpa.postId = comentario.getPostId().getId();
              cJpa.autorId = comentario.getAutorId().getValue();
              cJpa.texto = comentario.getTexto();
              cJpa.data = comentario.getData();
              cJpa.oculto = comentario.isOculto();
              return cJpa;
            })
            .collect(Collectors.toList());

        jpa.curtidas = source.getCurtidas().stream()
            .map(curtida -> {
              CurtidaJpa cJpa = new CurtidaJpa();
              cJpa.postId = curtida.getPostId().getId();
              cJpa.contaId = curtida.getContaId().getValue();
              return cJpa;
            })
            .collect(Collectors.toList());

        return jpa;
      }
    });

    addConverter(new AbstractConverter<BibliotecaJpa, Biblioteca>() {
      @Override
      protected Biblioteca convert(BibliotecaJpa source) {
        var contaId = new ContaId(source.contaId);
        Biblioteca biblioteca = new Biblioteca(contaId);

        for (ItemBibliotecaJpa itemJpa : source.itens) {
          try {
            var jogoId = new JogoId(itemJpa.jogoId);
            biblioteca.adicionarJogo(itemJpa.modeloDeAcesso, jogoId);

            var itemOpt = biblioteca.buscarJogoEmBiblioteca(jogoId);
            if (itemOpt.isPresent()) {
              var item = itemOpt.get();

              var dataField = ItemBiblioteca.class.getDeclaredField("dataAdicao");
              dataField.setAccessible(true);
              dataField.set(item, itemJpa.dataAdicao);

              if (itemJpa.baixado) {
                biblioteca.baixouJogo(item);
              }
            }
          } catch (Exception e) {
            throw new RuntimeException("Erro ao converter ItemBiblioteca", e);
          }
        }
        return biblioteca;
      }
    });

    addConverter(new AbstractConverter<Biblioteca, BibliotecaJpa>() {
      @Override
      protected BibliotecaJpa convert(Biblioteca source) {
        BibliotecaJpa jpa = new BibliotecaJpa();
        jpa.contaId = source.getContaId().getValue();

        IteratorBiblioteca<ItemBiblioteca> iterator = source.criarIterator();
        while (iterator.existeProximo()) {
          ItemBiblioteca item = iterator.proximo();
          ItemBibliotecaJpa itemJpa = new ItemBibliotecaJpa();
          itemJpa.biblioteca = jpa;
          itemJpa.jogoId = item.getJogoId().getValue();
          itemJpa.modeloDeAcesso = item.getModeloDeAcesso();
          itemJpa.dataAdicao = item.getDataAdicao();

          itemJpa.baixado = source.getItensBaixados().contains(item);

          jpa.itens.add(itemJpa);
        }
        return jpa;
      }
    });

    addConverter(new AbstractConverter<ModJpa, Mod>() {
      @Override
      protected Mod convert(ModJpa source) {
        try {
          List<VersaoMod> versoes = source.versoes.stream()
              .map(v -> new VersaoMod(
                v.getNotasDeAtualizacao(),
                v.getArquivo(),
                v.getDataDeEnvio()))
              .collect(Collectors.toList());
          
          return new Mod(
            source.getId(),
            new JogoId(source.getJogoId()),
            new ContaId(source.getAutorId()),
            source.getNome(),
            source.getDescricao(),
            StatusMod.valueOf(source.getStatus()),
            versoes
          );
        } catch (Exception e) {
          throw new RuntimeException("Erro ao converter ModJpa para Mod", e);
        }
      }
    });

    addConverter(new AbstractConverter<Mod, ModJpa>() {
      @Override
      protected ModJpa convert(Mod source) {
        try {
          ModJpa jpa = new ModJpa();
          jpa.id = source.getId();
          jpa.jogoId = source.getJogoId().getValue();
          jpa.autorId = source.getAutorId().getValue();
          jpa.nome = source.getNome();
          jpa.descricao = source.getDescricao();
          jpa.status = source.getStatus().name();

          jpa.versoes = source.getVersoes().stream()
              .map(v -> new VersaoModJpa(
                v.getNotasDeAtualizacao(),
                v.getArquivo(),
                v.getDataDeEnvio()))
              .collect(Collectors.toList());
          
          return jpa;
        } catch (Exception e) {
          throw new RuntimeException("Erro ao converter Mod para ModJpa", e);
        }
      }
    });
  }

  @Override
  public <D> D map(Object source, Class<D> destinationType) {
    return source != null ? super.map(source, destinationType) : null;
  }
}
