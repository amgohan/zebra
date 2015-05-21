# Zebra ![](https://travis-ci.org/amgohan/zebra.svg?branch=master)
DIY objects mapping and use zebra for unified way to inject mappers.
* for the sake of performance, you do objects mapping manually and don't know how organize your mappers. Zebra come with a simple way to :
  * create your mappers
  * manage one way and reverse mapping in one class
  * register your mappers and reuse them everywhere
  * manage deep mapping

zebra-1.0.0.jar is only 7.8K

[See the whole example with unit tests here](https://github.com/amgohan/zebra-examples/tree/master/zebra-javaonly)

Maven
=====
```xml
    <dependency>
        <groupId>com.agileasoft</groupId>
        <artifactId>zebra</artifactId>
        <version>1.0.0</version>
    </dependency>
```

How-to
============
Create **mappers** and use zebra to inject them in a simple way to map from a source object to a destination object and vise versa **using only java without any other framework**.

Assume that we have DTOs UserDto, RoleDto and domain objects User and Role as following :

```java
public class UserDto {

	private String firstName;

	private String lastName;

	private String username;

	private String password;

	private String birthdate;
	
	private List<RoleDto> roles;
	
	// ... getters and setters goes here
}
```

```java
public enum RoleDto {

	ADMIN("Administrator"),
	USER("Simple User"),
	SUPER_ADMIN("Super User"),
	MANAGER("Team manager");

	private String description;

	RoleDto(final String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}
```

```java
public class User {

	private String fullName;

	private String email;

	private String password;

	private Date birthDate;

	private List<Role> roles;
	
	// ... getters and setters goes here
}
```

```java
public class Role {

	private String code;

	private String label;
	
  // ... getters and setters goes here
}
```

Now if we want to map UserDto to User and vis versa with deep mapping (that mean also map List<RoleDto> to List<Role>) we must follow this steps :

#### 1. Create RoleDtoEntityMapper :
```java
public class RoleDtoEntityMapper extends Mapper<RoleDto, Role> {

	@Override
	public Role mapAToB(final RoleDto dto) {
		final Role entity = new Role();
		entity.setCode(dto.name());
		entity.setLabel(dto.getDescription());
		return entity;
	}

	@Override
	public RoleDto mapBToA(final Role entity) {
		return RoleDto.valueOf(entity.getCode());
	}
}
```

#### 2. Create UserDtoEntityMapper :

```java
public class UserDtoEntityMapper extends Mapper<UserDto, User> {

	private final static String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

	@Override
	public User mapAToB(final UserDto dto) {
		final User entity = new User();

		entity.setFullName(dto.getFirstName() + ", " + dto.getLastName());

		entity.setEmail(dto.getUsername());

		entity.setBirthDate(this.parseDate(dto.getBirthdate()));

		entity.setPassword(dto.getPassword());

		// deep mapping : here we call the mapper to map RoleDto to Role using the injected mapper RoleDtoEntityMapper
		entity.setRoles(this.mapper.map(dto.getRoles(), Role.class));

		return entity;
	}

	@Override
	public UserDto mapBToA(final User entity) {
		final UserDto dto = new UserDto();

		dto.setBirthdate(this.formatDate(entity.getBirthDate()));

		final String[] fullNameSplit = entity.getFullName().split(", ");

		dto.setFirstName(fullNameSplit[0]);

		dto.setLastName(fullNameSplit[1]);

		dto.setUsername(entity.getEmail());

		dto.setPassword(entity.getPassword());

		// deep mapping : here we call the mapper to map Role to RoleDto using the injected mapper RoleDtoEntityMapper
		dto.setRoles(this.mapper.map(entity.getRoles(), RoleDto.class));

		return dto;
	}

	private Date parseDate(final String date) {
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		try {
			return sdf.parse(date);
		} catch (final ParseException exception) {
			return null;
		}
	}

	private String formatDate(final Date date) {
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		return sdf.format(date);
	}
}
```

Every mapper must extend the framework class Mapper<A, B> and override mapAToB and if needed override also mapBToA for reverse mapping.
The clean and reusable way to map a complex object inside a mapper is to simply call the mapper attribute ```this.mapper.map(entity.getRoles(), RoleDto.class)``` which will inject the right (already registred) mapper and do the mappping. A clean way and reusable anywhere in your code.

#### 3. Register mappers :
  **a. Pure java :**
  
We will create a **MapperFactorySingleton** which will register all mappers and build a **MapperProcessor** :
```java
public class MapperFactorySingleton {

	private static MapperFactorySingleton instance;

	private final MapperProcessor mapper;

	// private constructor
	private MapperFactorySingleton() {

		final MapperFactory factory = new MapperFactory();
		this.mapper = factory.registerAll(this.listMappers()).build();
	}

	public static MapperFactorySingleton getInstance() {

		if (MapperFactorySingleton.instance == null) {
			MapperFactorySingleton.instance = new MapperFactorySingleton();
		}

		return MapperFactorySingleton.instance;
	}

	public MapperProcessor getMapper() {

		return this.mapper;
	}

	@SuppressWarnings("rawtypes")
	private List<Mapper> listMappers() {

		final List<Mapper> listMappers = new ArrayList<>();

		// register manually your mappers here.
		listMappers.add(new UserDtoEntityMapper());
		listMappers.add(new RoleDtoEntityMapper());
		// ... add other mappers here.
		return listMappers;
	}
}
```

* **MapperFactorySingleton** register all your mappers (added manually in **listMappers** method) and create an instance of **MapperProcessor** which will be injected wherever you want to do a mapping from a source class A to a destination class B.
* So wherever you are in your code you can call the entry point of all your mappers by  ```MapperFactorySingleton.getInstance().getMapper()``` and then call the method ```map```
* Example :
```java
MapperProcessor mapper = MapperFactorySingleton.getInstance().getMapper();
UserDto userDto = new UserDto();
// call setters here to add some data to the userDto instance.
User user = mapper.map(userDto, User.class);
```
[See the whole example with unit tests here](https://github.com/amgohan/zebra-examples/tree/master/zebra-javaonly)
  
  **b. In Spring context :**

TODO : coming soon

License
=======
Apache 2.0 License
