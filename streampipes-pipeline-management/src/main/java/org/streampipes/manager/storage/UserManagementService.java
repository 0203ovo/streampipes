/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.storage;

import org.streampipes.model.client.user.RegistrationData;
import org.streampipes.model.client.user.Role;
import org.streampipes.model.client.user.User;
import org.streampipes.storage.management.StorageDispatcher;
import org.streampipes.user.management.util.PasswordUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

public class UserManagementService {

  public Boolean registerUser(RegistrationData data, Set<Role> roles) {
    User user = new User(data.getEmail(), data.getPassword(), roles);

    try {
      String encryptedPassword = PasswordUtil.encryptPassword(data.getPassword());
      user.setPassword(encryptedPassword);
      StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI().storeUser(user);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      return false;
    }

    return true;
  }

  public static UserService getUserService() {
    return new UserService(StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI());
  }

}
