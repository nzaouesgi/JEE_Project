package fr.esgi.secureupload.users.adapters.helpers;

import fr.esgi.secureupload.users.ports.RandomTokenGenerator;
import fr.esgi.secureupload.common.utils.Utils;

public class RandomTokenGeneratorImpl implements RandomTokenGenerator {

    @Override
    public String generate(int bytesLength) {
        return Utils.randomBytesToHex(bytesLength );
    }
}
