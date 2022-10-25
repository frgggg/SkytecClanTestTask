package zhar_feda.skytec.clan_test_task.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhar_feda.skytec.clan_test_task.models.Clan;
import zhar_feda.skytec.clan_test_task.dao.ClanDao;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/clan")
public class ClanController {
    private final ClanDao clanDao;

    @Autowired
    public ClanController(ClanDao clanDao) {
        this.clanDao = clanDao;
    }

    @GetMapping("/id/{id}")
    public Clan findById(@PathVariable Long id) {
        return clanDao.findById(id);
    }

    @GetMapping("/name/{name}")
    public Clan findById(@PathVariable String name) {
        return clanDao.findByName(name);
    }

    @GetMapping
    public List<Clan> findAll(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) {
        return clanDao.findAll(page, pageSize);
    }

    @PostMapping
    public Clan create(@RequestBody String name) {
        log.info("Try create new clan: {}", name);
        Clan result = clanDao.saveNew(name);
        log.info("New clan: {}", result);
        return result;
    }
}
