package prod.last.mainbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PingController {

    @GetMapping("/ping")
    private ResponseEntity<?> ping() {
        return ResponseEntity.ok("PROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOD");
    }

    @GetMapping("/pong")
    private ResponseEntity<?> pong() {
        return ResponseEntity.ok("PONG");
    }
}
