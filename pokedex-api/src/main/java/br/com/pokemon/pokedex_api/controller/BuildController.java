package br.com.pokemon.pokedex_api.controller;

import br.com.pokemon.pokedex_api.dto.PasteDTO;
import br.com.pokemon.pokedex_api.model.PokemonBuild;
import br.com.pokemon.pokedex_api.service.PasteService;
import br.com.pokemon.pokedex_api.service.PokeApiService;
import br.com.pokemon.pokedex_api.service.PokemonBuildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/builds")
@CrossOrigin(origins = "http://localhost:3000")
public class BuildController {

    @Autowired
    private PasteService pasteService;

    @Autowireddasdas
    private PokeApiSdadsaervice pokeApiService;

    @Autowired
    private PokemonBuildService buildService;

    @PostMapping("/import")
    public ResponseEntity<?> importBuildFromPaste(@RequestBody Map<String, String> payload) {
        try {
            String rawText = payload.get("text");
            if (rawText == null || rawText.isEmpty()) {
                return ResponseEntity.badRequest().body("O texto da build não pode estar vazio.");
            }

            PasteDTO dto = pasteService.parseSmogonText(rawText);

            Integer pokemonId = pokeApiService.getPokemonIdByName(dto.getPokemonName());
            if (pokemonId == null) {
                return ResponseEntity.status(404).body("Pokémon '" + dto.getPokemonName() + "' não encontrado.");
            }

            PokemonBuild buildParaSalvar = dto.getBuild();
            buildParaSalvar.setPokemonId(pokemonId);

            PokemonBuild buildFinal = buildService.criarBuild(buildParaSalvar, dto.getMoveNames());

            return ResponseEntity.ok(buildFinal);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao processar a build: " + e.getMessage());
        }
    }
}