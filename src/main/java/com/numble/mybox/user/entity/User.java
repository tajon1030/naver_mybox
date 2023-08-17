package com.numble.mybox.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.unit.DataSize;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String email;
    private String password;
    private Long unusedQuota = DataSize.ofGigabytes(30L).toBytes();

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User useQuota(long bytes) {
        this.unusedQuota += bytes;
        return this;
    }

    public User returnQuota(long bytes){
        this.unusedQuota -= bytes;
        return this;
    }
}
