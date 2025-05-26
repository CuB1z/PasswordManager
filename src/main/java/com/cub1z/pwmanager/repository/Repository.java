package com.cub1z.pwmanager.repository;

import java.io.IOException;

public interface Repository {
    void load() throws Exception;
    void save() throws IOException;
}
