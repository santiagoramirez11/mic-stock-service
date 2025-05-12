package com.banreservas.micstockservice;

import com.banreservas.micstockservice.container.DockerStartStopExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@ExtendWith({SpringExtension.class, DockerStartStopExtension.class})
@SpringBootTest(classes = {MicStockServiceApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MicStockServiceApplicationTests {

}
