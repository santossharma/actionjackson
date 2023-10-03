package de.baro.actionjackson.account;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by santoshsharma on 01 Oct, 2023
 */

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final ObjectMapper objectMapper;

    private Set<Account> accounts = new CopyOnWriteArraySet<>();

    public AccountController(Jackson2ObjectMapperBuilder builder) {
        this.objectMapper = builder.indentOutput(true).build();

        System.out.println("Customized Pretified : " +
                objectMapper.getSerializationConfig().isEnabled(SerializationFeature.INDENT_OUTPUT));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAccount(@RequestBody Account account) {
        accounts.add(account);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> findAccount(@PathVariable Integer id) {
        return accounts.stream().filter(account -> id.equals(account.id())).findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build());
    }

    @GetMapping
    public Collection<Account> findAll() {
        return accounts;
    }

    @GetMapping("/view-summary")
    @JsonView(View.Summary.class)
    public Collection<Account> findAllSummaryView() {
        return accounts;
    }
    @GetMapping("/view-extended")
    @JsonView(View.ExtendedSummary.class)
    public Collection<Account> findAllExtendedView() {
        return accounts;
    }

}
