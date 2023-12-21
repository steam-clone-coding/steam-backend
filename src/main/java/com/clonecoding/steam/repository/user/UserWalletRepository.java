package com.clonecoding.steam.repository.user;

import com.clonecoding.steam.entity.user.UserWallet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWalletRepository extends CrudRepository<UserWallet, Long> {
}
