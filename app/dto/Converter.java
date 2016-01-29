package dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Address;
import models.Company;
import models.User;
import models.UserRole;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Olga on 27.01.2016.
 */
public class Converter {
    private Map<String, Long> roles;

    public Converter() {
        roles = new HashMap<>();
        roles.put("Admin", 2L);
        roles.put("Dispatcher", 3L);
        roles.put("Manager", 4L);
        roles.put("Driver", 5L);
    }

    public User convertUserDTOToUser(UserDTO userDTO, User user) {
        user.username = userDTO.username;
        user.name = userDTO.name;
        user.surname = userDTO.surname;
        user.patronymic = userDTO.patronymic;
        user.birthday = userDTO.birthday;
        user.email = userDTO.email;
        if (userDTO.country != null && userDTO.city != null && userDTO.street != null && userDTO.house != null && userDTO.flat != null) {
            user.address = new Address();
            user.address.country = userDTO.country;
            user.address.city = userDTO.city;
            user.address.street = userDTO.street;
            user.address.house = userDTO.house;
            user.address.flat = userDTO.flat;
        }
        UserRole role = new UserRole();
        role.id = roles.get(userDTO.roles);
        role.name = userDTO.roles.toUpperCase();
        user.userRoleList = new ArrayList<>();
        user.userRoleList.add(role);
        user.password = BCrypt.hashpw(userDTO.password, BCrypt.gensalt());
        user.company = new Company();
        user.deleted = false;
        return user;
    }

    public UserDTO convertJsonToAccountDTO(String json, UserDTO user) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        user = mapper.readerWithView(Views.Account.class)
                .forType(UserDTO.class)
                .readValue(json);
        return user;
    }

    public UserDTO convertJsonToEmployeeDTO(String json, UserDTO user) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        user = mapper.readerWithView(Views.Employee.class)
                .forType(UserDTO.class)
                .readValue(json);
        return user;
    }

    public String convertAccountDTOToJson(String json, UserDTO user) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        json = mapper.writerWithView(Views.Account.class).writeValueAsString(user);
        return json;
    }

    public String convertEmployeeDTOToJson(String json, UserDTO user) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        json = mapper.writerWithView(Views.Employee.class).writeValueAsString(user);
        return json;
    }
}