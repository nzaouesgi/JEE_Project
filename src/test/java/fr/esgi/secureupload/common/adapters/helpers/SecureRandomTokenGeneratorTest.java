package fr.esgi.secureupload.common.adapters.helpers;

import fr.esgi.secureupload.common.domain.ports.RandomTokenGenerator;
import fr.esgi.secureupload.common.infrastructure.adapters.helpers.SecureRandomTokenGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SecureRandomTokenGeneratorTest {

    RandomTokenGenerator generator;

    public SecureRandomTokenGeneratorTest(){
        this.generator = new SecureRandomTokenGenerator();
    }

    @Test
    public void generate_ShouldRe1turnRandomHexBytes (){
        String generated = generator.generate(32);
        Assertions.assertNotNull(generated);
        Assertions.assertEquals(64, generated.length());
    }
}
