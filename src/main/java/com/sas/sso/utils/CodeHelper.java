package com.sas.sso.utils;

import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CodeHelper {
	public static String MAIN_PACKAGE = "com.sas";
	public static String MAIN_PACKAGE_PATH = "src/main/java/com/sas/";

	private static final String header = "/**\n * @author shahzad.hussain \n */\n";

	public static void main(String[] args) {
		List<TableData> tableList = new ArrayList<>();
		tableList.add(new TableData("departmentName", "String", "", true, false));
		tableList.add(new TableData("departmentAddress", "String", "", true, false));
		tableList.add(new TableData("departmentCode", "String", "", true, false));
		tableList.add(new TableData("deparmentId", "Long", "", true, false));
		createCollection("Test", tableList);
	}

	private static void createCollection(String entityName, List<TableData> fieldsMap) {
		createEntity(MAIN_PACKAGE_PATH + "entity/", entityName, fieldsMap);
		createRepository(MAIN_PACKAGE_PATH + "repository/", entityName, fieldsMap);
		createPojo(MAIN_PACKAGE_PATH + "pojos/", entityName, fieldsMap);
		createRequest(MAIN_PACKAGE_PATH + "request/", entityName);
		createResponse(MAIN_PACKAGE_PATH + "response/", entityName);
		createService(MAIN_PACKAGE_PATH + "service/", entityName, fieldsMap);
		createServiceImpl(MAIN_PACKAGE_PATH + "service/impl/", entityName, fieldsMap);
		createController(MAIN_PACKAGE_PATH + "controller/", entityName, fieldsMap);
		createConverter(MAIN_PACKAGE_PATH + "assembler/", entityName, fieldsMap);
	}

	private static void createRepository(String folder, String fileName, List<TableData> fieldsMap) {

		try {
			FileWriter fileWriter = new FileWriter(folder + fileName + "Repository.java");
			fileWriter.write("package " + MAIN_PACKAGE + ".repository;\n");
			fileWriter.write("\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".entity." + fileName + ";\n");
			fileWriter.write("import org.springframework.data.jpa.repository.JpaRepository;\n");
			fileWriter.write("\n");
			fileWriter.write(header);
			fileWriter.write("\n");
			fileWriter.write("public interface " + fileName);
			fileWriter.write("Repository extends JpaRepository<" + fileName + ", Long>{\n");
			fileWriter.write("}");
			fileWriter.flush();
			fileWriter.close();
			System.out.println("Repository Created!");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createController(String folder, String fileName, List<TableData> fieldsMap) {
		try {
			FileWriter fileWriter = new FileWriter(folder + fileName + "Controller.java");
			fileWriter.write("package " + MAIN_PACKAGE + ".controller;\n\n");
			fileWriter.write("import org.springframework.beans.factory.annotation.Autowired;\n");
			fileWriter.write("import org.springframework.web.bind.annotation.RestController;\n");
			fileWriter.write("import org.springframework.web.bind.annotation.RequestBody;\n");
			fileWriter.write("import org.springframework.web.bind.annotation.PathVariable;\n");
			fileWriter.write("import org.springframework.web.bind.annotation.RequestMapping;\n");
			fileWriter.write("import org.springframework.web.bind.annotation.DeleteMapping;\n");
			fileWriter.write("import org.springframework.web.bind.annotation.GetMapping;\n");
			fileWriter.write("import org.springframework.web.bind.annotation.PatchMapping;\n");
			fileWriter.write("import org.springframework.web.bind.annotation.PostMapping;\n");
			fileWriter.write("import org.springframework.http.ResponseEntity;\n");
			fileWriter.write("import org.springframework.http.HttpStatus;\n");
			fileWriter.write("import com.sas.sso.constants.Constant;\n");
			fileWriter.write("import org.springframework.util.CollectionUtils;\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".constants.RestMappingConstants;\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".dto.Response;\n");

			fileWriter.write("import " + MAIN_PACKAGE + ".exception.ObjectNotFoundException;\n");

			fileWriter.write("import " + MAIN_PACKAGE + ".dto." + fileName + "Request;\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".dto." + fileName + "Response;\n");
			fileWriter.write("import java.util.List;\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".service." + fileName + "Service;\n");
			fileWriter.write("\n");
			fileWriter.write(header);
			fileWriter.write("@RestController\n");
			fileWriter.write("@RequestMapping(value = \"/" + fileName.toLowerCase().charAt(0)
					+ fileName.substring(1, fileName.length()) + "\")\n");
			fileWriter.write("public class " + fileName + "Controller implements Constant{\n\n");

			fileWriter.write("\t@Autowired\n");
			String serviceName = fileName.toLowerCase().charAt(0) + fileName.substring(1, fileName.length())
					+ "Service";
			fileWriter.write("\tprivate " + fileName + "Service " + serviceName + ";\n");
			fileWriter.write("\n");
			fileWriter.write("\t@PostMapping\n");
			fileWriter.write("\tpublic ResponseEntity<Response> add(");
			fileWriter.write("@RequestBody(required = true) " + fileName + "Request request");
			fileWriter.write(")throws ObjectNotFoundException{\n");
			for (TableData tableData : fieldsMap) {
				if (tableData.isUnique()) {
					fileWriter.write("\tif (" + serviceName + ".existBy" + getMethodeName(tableData.getName())
							+ "(request.get" + getMethodeName(tableData.getName()) + "())){\n");
					fileWriter.write("\t\tthrow new ObjectNotFoundException(FmsResponseCode.EXIST_"
							+ getUppercase(tableData.getName()) + ");\n");
					fileWriter.write("\t}\n");
				}
			}
			fileWriter.write("\tLong response = " + serviceName + ".add(request);\n");
			fileWriter.write(
					"\treturn new ResponseEntity<>(new Response(\"Success\", response, HttpStatus.OK), HttpStatus.OK);\n");
			fileWriter.write("\t}\n\n");

			fileWriter.write("\t@GetMapping(path = RestMappingConstants.ID_PARAM)\n");
			fileWriter.write("\tpublic ResponseEntity<Response> getById(");
			fileWriter.write("@PathVariable(RestMappingConstants.ID) Long id)throws ObjectNotFoundException{\n");
			fileWriter.write("\t" + fileName + "Response response = " + serviceName + ".getById(id);\n");
			fileWriter.write("\tif(response==null){\n");
			fileWriter.write("\t\tthrow new ObjectNotFoundException(\"Record not found\", HttpStatus.NOT_FOUND);\n");
			fileWriter.write("\t}\n");
			fileWriter.write(
					"\treturn new ResponseEntity<>(new Response(\"Success\", response, HttpStatus.OK), HttpStatus.OK);\n");
			fileWriter.write("\t}\n\n");

			fileWriter.write("\t@GetMapping\n");
			fileWriter.write("\tpublic ResponseEntity<Response> getAll()throws ObjectNotFoundException{\n");
			fileWriter.write("\tList<" + fileName + "Response> response = " + serviceName + ".getAll();\n");
			fileWriter.write("\tif(CollectionUtils.isEmpty(response)){\n");
			fileWriter.write("\t\tthrow new ObjectNotFoundException(\"Record not found\", HttpStatus.NOT_FOUND);\n");
			fileWriter.write("\t}\n");
			fileWriter.write(
					"\treturn new ResponseEntity<>(new Response(\"Success\", response, HttpStatus.OK), HttpStatus.OK);\n");
			fileWriter.write("\t}\n\n");

			fileWriter.write("\t@DeleteMapping(path = RestMappingConstants.ID_PARAM)\n");
			fileWriter.write("\tpublic ResponseEntity<Response> delete(");
			fileWriter.write("@PathVariable(RestMappingConstants.ID) Long id)throws ObjectNotFoundException{\n");
			fileWriter.write("\tif(!" + serviceName + ".exist(id)){\n");
			fileWriter.write("\t\tthrow new ObjectNotFoundException(\"Record not found\", HttpStatus.NOT_FOUND);\n");
			fileWriter.write("\t}\n");
			fileWriter.write("\t" + serviceName + ".delete(id);\n");
			fileWriter
					.write("\treturn new ResponseEntity<>(new Response(\"Success\", HttpStatus.OK), HttpStatus.OK);\n");
			fileWriter.write("\t}\n\n");

			fileWriter.write("\t@PatchMapping\n");
			fileWriter.write("\tpublic ResponseEntity<Response> update(");
			fileWriter.write(
					"@RequestBody(required = true) " + fileName + "Response request)throws ObjectNotFoundException{\n");
			fileWriter.write("\tif(!" + serviceName + ".exist(request.get" + fileName + "Id())){\n");
			fileWriter.write("\t\tthrow new ObjectNotFoundException(\"Record not found\", HttpStatus.NOT_FOUND);\n");
			fileWriter.write("\t}\n");
			fileWriter.write("\tBoolean response = " + serviceName + ".update(request);\n");
			fileWriter.write(
					"\treturn new ResponseEntity<>(new Response(\"Success\", response, HttpStatus.OK), HttpStatus.OK);\n");
			fileWriter.write("\t}\n\n");

			fileWriter.write("}");
			fileWriter.flush();
			fileWriter.close();
			System.out.println("Controller Created!");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createServiceImpl(String folder, String fileName, List<TableData> fieldsMap) {
		try {
			FileWriter fileWriter = new FileWriter(folder + fileName + "ServiceImpl.java");
			fileWriter.write("package " + MAIN_PACKAGE + ".service;\n\n");
			fileWriter.write("import org.springframework.beans.factory.annotation.Autowired;\n");
			fileWriter.write("import org.springframework.stereotype.Service;\n\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".entity." + fileName + ";\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".dto." + fileName + "Request;\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".dto." + fileName + "Response;\n");
			fileWriter.write("import java.util.List;\n");
			fileWriter.write("import java.util.Optional;\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".repository." + fileName + "Repository;\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".utils." + fileName + "Converter;\n\n");
			fileWriter.write(header);
			fileWriter.write("@Service\n");
			fileWriter.write("public class " + fileName);
			fileWriter.write("ServiceImpl implements " + fileName + "Service{\n\n");
			fileWriter.write("\t@Autowired\n");
			String daoName = fileName.toLowerCase().charAt(0) + fileName.substring(1, fileName.length()) + "Repository";
			fileWriter.write("\tprivate " + fileName + "Repository " + daoName + ";\n\n");

			fileWriter.write("\t@Override\n");
			fileWriter.write("\tpublic Long add(" + fileName + "Request request){\n");
			fileWriter.write("\t" + fileName + " entity=" + fileName + "Converter.getEntityFromRequest(request);\n");
			fileWriter.write("\tif(entity!=null){\n");
			fileWriter.write("\t\t" + daoName + ".save(entity);\n");
			fileWriter.write("\t\treturn entity.getId();\n");
			fileWriter.write("\t}\n");
			fileWriter.write("\treturn 0l;\n");
			fileWriter.write("\t}\n\n");

			fileWriter.write("\t@Override\n");
			fileWriter.write("\tpublic " + fileName + "Response getById(Long id){\n");
			fileWriter.write("\treturn " + fileName + "Converter.getResponseFromEntity(" + daoName
					+ ".findById(id).orElse(null));\n");
			fileWriter.write("\t}\n\n");

			fileWriter.write("\t@Override\n");
			fileWriter.write("\tpublic List<" + fileName + "Response> getAll(){\n");
			fileWriter.write(
					"\treturn " + fileName + "Converter.getResponseListFromEntityList(" + daoName + ".findAll());\n");
			fileWriter.write("\t}\n\n");

			fileWriter.write("\t@Override\n");
			fileWriter.write("\tpublic void delete(Long id){\n");
			fileWriter.write("\t" + daoName + ".deleteById(id);\n");
			fileWriter.write("\t}\n\n");

			fileWriter.write("\t@Override\n");
			fileWriter.write("\tpublic boolean exist(Long id){\n");
			fileWriter.write("\t\tif (getById(id) != null){\n");
			fileWriter.write("\t\t\treturn true;\n");
			fileWriter.write("\t\t}\n");
			fileWriter.write("\t\treturn false;\n");
			fileWriter.write("\t}\n\n");

			fileWriter.write("\t@Override\n");
			fileWriter.write("\tpublic boolean update(" + fileName + "Response request){\n");
			fileWriter.write("\t\t" + "Optional<" + fileName + "> findById = " + daoName + ".findById(request.get"
					+ fileName + "Id());\n");

			fileWriter.write("\t\tif (findById.isPresent()) {\n");
			fileWriter.write("\t\t\t" + fileName + " entity = findById.get();\n");
			fileWriter.write("\t\t\t" + fileName + "Converter.getEntityFromResponse(request,entity);\n");
			fileWriter.write("\t\t\tif (entity != null) {\n");
			fileWriter.write("\t\t\t\t" + daoName + ".save(entity);\n");
			fileWriter.write("\t\t\t\treturn true;\n");
			fileWriter.write("\t\t\t}\n");
			fileWriter.write("\t\t}\n");
			fileWriter.write("\t\treturn false;\n");
			fileWriter.write("\t}\n\n");
			for (TableData data : fieldsMap) {
				if (data.isUnique()) {
					fileWriter.write("\tpublic boolean existBy" + getMethodeName(data.getName()) + "(" + data.getType()
							+ " " + data.getName() + "){\n");
					fileWriter.write(
							"\tif(getBy" + getMethodeName(data.getName()) + "(" + data.getName() + ") != null){\n");
					fileWriter.write("\t\treturn true;\n");
					fileWriter.write("\t}\n");
					fileWriter.write("\treturn false;\n");
					fileWriter.write("\t}\n\n");

					fileWriter.write("\tpublic " + fileName + "Response getBy" + getMethodeName(data.getName()) + "("
							+ data.getType() + " " + data.getName() + "){\n");
					fileWriter.write("\treturn " + fileName + "Converter.getResponseFromEntity(" + daoName + ".getBy"
							+ getMethodeName(data.getName()) + "(" + data.getName() + "));");
					fileWriter.write("\t}\n\n");
				}
			}

			fileWriter.write("}");
			fileWriter.flush();
			fileWriter.close();
			System.out.println("ServiceImpl Created!");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createService(String folder, String fileName, List<TableData> fieldsMap) {
		try {
			FileWriter fileWriter = new FileWriter(folder + fileName + "Service.java");
			fileWriter.write("package " + MAIN_PACKAGE + ".service;\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".dto." + fileName + "Request;\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".dto." + fileName + "Response;\n");
			fileWriter.write("import java.util.List;\n\n");
			fileWriter.write(header);
			fileWriter.write("public interface " + fileName);
			fileWriter.write("Service{\n");
			fileWriter.write("\tLong add(" + fileName + "Request request);\n");
			fileWriter.write("\t" + fileName + "Response getById(Long id);\n");
			fileWriter.write("\tList<" + fileName + "Response> getAll();\n");
			fileWriter.write("\tvoid delete(Long id);\n");
			fileWriter.write("\tboolean exist(Long id);\n");
			fileWriter.write("\tboolean update(" + fileName + "Response request);\n");
			for (TableData data : fieldsMap) {
				if (data.isUnique()) {
					fileWriter.write("\tboolean existBy" + getMethodeName(data.getName()) + "(" + data.getType() + " "
							+ data.getName() + ");\n");
					fileWriter.write("\t" + fileName + "Response getBy" + getMethodeName(data.getName()) + "("
							+ data.getType() + " " + data.getName() + ");\n");
				}
			}
			fileWriter.write("}");
			fileWriter.flush();
			fileWriter.close();
			System.out.println("Service Created!");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createConverter(String folder, String fileName, List<TableData> fieldsMap) {
		try {
			FileWriter fileWriter = new FileWriter(folder + fileName + "Converter.java");
			fileWriter.write("package " + MAIN_PACKAGE + ".utils;\n\n");

			fileWriter.write("import " + MAIN_PACKAGE + ".entity." + fileName + ";\n");
			fileWriter.write("import java.util.ArrayList;\n");
			fileWriter.write("import java.util.List;\n");
			fileWriter.write("import org.springframework.util.CollectionUtils;\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".dto." + fileName + "Request;\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".dto." + fileName + "Response;\n\n");
			fileWriter.write(header);
			fileWriter.write("public class " + fileName);
			fileWriter.write("Converter {\n");

			fileWriter.write("\n\tpublic static " + fileName + "Request getSample() {\n");
			fileWriter.write("\t\t" + fileName + "Request response = new " + fileName + "Request();\n");
			for (TableData entry : fieldsMap) {
				System.out.println("name:" + entry.getName());
				fileWriter.write("\t\tresponse.set" + getMethodeName(entry.getName()) + "("
						+ getSample(entry.getType(), entry.getName()) + ");\n");
			}
			fileWriter.write("\t\treturn response;\n");
			fileWriter.write("\t}\n");

			fileWriter.write(
					"\n\tpublic static " + fileName + " getEntityFromRequest(" + fileName + "Request request) {\n");
			fileWriter.write("\t\tif(request!=null){\n");
			fileWriter.write("\t\t\t" + fileName + " response = new " + fileName + "();\n");
			for (TableData entry : fieldsMap) {
				fileWriter.write("\t\t\tresponse.set" + getMethodeName(entry.getName()) + "(request.get"
						+ getMethodeName(entry.getName()) + "());\n");
			}
			fileWriter.write("\t\t\treturn response;\n");
			fileWriter.write("\t\t}\n");
			fileWriter.write("\t\treturn null;\n");
			fileWriter.write("\t}\n");

			fileWriter.write(
					"\n\tpublic static " + fileName + "Response getResponseFromEntity(" + fileName + " request) {\n");
			fileWriter.write("\t\tif(request!=null){\n");
			fileWriter.write("\t\t\t" + fileName + "Response response = new " + fileName + "Response();\n");
			fileWriter.write("\t\t\tresponse.set" + fileName + "Id(request.getId());\n");
			for (TableData entry : fieldsMap) {
				fileWriter.write("\t\t\tresponse.set" + getMethodeName(entry.getName()) + "(request.get"
						+ getMethodeName(entry.getName()) + "());\n");
			}
			fileWriter.write("\t\t\treturn response;\n");
			fileWriter.write("\t\t}\n");
			fileWriter.write("\t\treturn null;\n");
			fileWriter.write("\t}\n");

			fileWriter.write("\n\tpublic static " + fileName + " getEntityFromResponse(" + fileName
					+ "Response request," + fileName + " response) {\n");
			fileWriter.write("\t\tif (request!=null){\n");
			for (TableData entry : fieldsMap) {
				fileWriter.write("\t\t\tresponse.set" + getMethodeName(entry.getName()) + "(request.get"
						+ getMethodeName(entry.getName()) + "());\n");
			}
			fileWriter.write("\t\t\treturn response;\n");
			fileWriter.write("\t\t}\n");
			fileWriter.write("\t\treturn null;\n");
			fileWriter.write("\t}\n");

			fileWriter.write("\n\tpublic static List<" + fileName + "Response> getResponseListFromEntityList(List<"
					+ fileName + "> requestList) {\n");
			fileWriter.write("\t\tif (!CollectionUtils.isEmpty(requestList)){\n");
			fileWriter.write("\t\t\tList<" + fileName + "Response> responseList = new ArrayList<>();\n");
			fileWriter.write("\t\t\tfor (" + fileName + " request:requestList){\n");
			fileWriter.write("\t\t\t\tresponseList.add(getResponseFromEntity(request));\n");
			fileWriter.write("\t\t\t}\n");
			fileWriter.write("\t\t\treturn responseList;\n");
			fileWriter.write("\t\t}\n");
			fileWriter.write("\t\treturn null;\n");
			fileWriter.write("\t}\n");

			fileWriter.write("}");
			fileWriter.flush();
			fileWriter.close();
			System.out.println("Converter Created!");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getSample(String value, String name) {
		switch (value) {
			case "String":
				return "\"" + name + "\"";
			case "Double":
			case "Float":
			case "Integer":
				return "123";
			case "Long":
				return "123l";
			case "Boolean":
				return "true";
			case "List<String>":
				return "new ArrayList<>()";
			default:
				return "new " + value + "()";
		}
	}

	private static void createResponse(String folder, String fileName) {
		try {
			FileWriter fileWriter = new FileWriter(folder + fileName + "Response.java");
			fileWriter.write("package " + MAIN_PACKAGE + ".dto;\n\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".pojos." + fileName + "Pojo;\n");
//			fileWriter.write("import lombok.Data;\n\n");
//			fileWriter.write("@Data\n");
			fileWriter.write("\n");
			fileWriter.write("public class " + fileName);
			fileWriter.write("Response extends " + fileName + "Pojo {\n");
			fileWriter.write("\tprivate Long " + fileName.toLowerCase().charAt(0)
					+ fileName.substring(1, fileName.length()) + "Id;\n");
			fileWriter.write("\n");
			fileWriter.write("\tpublic Long get" + fileName + "Id() {\n");
			fileWriter.write("\t\treturn " + fileName.toLowerCase().charAt(0) + fileName.substring(1, fileName.length())
					+ "Id;\n");
			fileWriter.write("\t}\n");
			fileWriter.write("\n");
			fileWriter.write("\tpublic void set" + fileName + "Id(Long " + fileName.toLowerCase().charAt(0)
					+ fileName.substring(1, fileName.length()) + "Id) {\n");
			fileWriter.write("\t\tthis." + fileName.toLowerCase().charAt(0) + fileName.substring(1, fileName.length())
					+ "Id = " + fileName.toLowerCase().charAt(0) + fileName.substring(1, fileName.length()) + "Id;\n");
			fileWriter.write("\t}\n");

			fileWriter.write("}");
			fileWriter.flush();
			fileWriter.close();
			System.out.println("Response Created!");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createRequest(String folder, String fileName) {
		try {
			FileWriter fileWriter = new FileWriter(folder + fileName + "Request.java");
			fileWriter.write("package " + MAIN_PACKAGE + ".dto;\n\n");
			fileWriter.write("import " + MAIN_PACKAGE + ".pojos." + fileName + "Pojo;\n");
//			fileWriter.write("import lombok.Data;\n\n");
//			fileWriter.write("@Data\n");
			fileWriter.write("public class " + fileName);
			fileWriter.write("Request extends " + fileName + "Pojo{\n");
			fileWriter.write("}");
			fileWriter.flush();
			fileWriter.close();
			System.out.println("Request Created!");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createPojo(String folder, String fileName, List<TableData> fieldsMap) {
		try {
			FileWriter fileWriter = new FileWriter(folder + fileName + "Pojo.java");
			fileWriter.write("package " + MAIN_PACKAGE + ".pojos;\n\n");
//			fileWriter.write("import lombok.Data;\n\n");
//			fileWriter.write("@Data\n");
			fileWriter.write("public class " + fileName);
			fileWriter.write("Pojo {\n");
			for (TableData data : fieldsMap) {
				fileWriter.write("\tprivate " + data.getType() + " " + data.getName() + ";\n");

			}
			fileWriter.write("\n");
			createGetter(fieldsMap, fileWriter);

			createSetter(fieldsMap, fileWriter);

			fileWriter.write("}\n");

			fileWriter.flush();
			fileWriter.close();
			System.out.println("Pojo Created!");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createGetter(List<TableData> fieldsMap, FileWriter fileWriter) throws IOException {
		for (TableData entry : fieldsMap) {
			fileWriter.write("\tpublic " + entry.getType() + " get" + getMethodeName(entry.getName()) + "() {\n");
			fileWriter.write("\t\treturn " + entry.getName() + ";\n");
			fileWriter.write("\t}\n");
			fileWriter.write("\n");
		}
	}

	private static void createSetter(List<TableData> fieldsMap, FileWriter fileWriter) throws IOException {
		for (TableData entry : fieldsMap) {
			fileWriter.write("\tpublic void" + " set" + getMethodeName(entry.getName()) + "(" + entry.getType() + " "
					+ entry.getName() + ") {\n");
			fileWriter.write("\t\tthis." + entry.getName() + " = " + entry.getName() + ";\n");
			fileWriter.write("\t}\n");
			fileWriter.write("\n");
		}
	}

	private static void createEntity(String folder, String fileName, List<TableData> fieldsMap) {
		try {
			FileWriter fileWriter = new FileWriter(folder + fileName + ".java");
			fileWriter.write("package " + MAIN_PACKAGE + ".entity;\n\n");
			fileWriter.write("import javax.persistence.Column;\n");
			fileWriter.write("import javax.persistence.Entity;\n");
			fileWriter.write("import javax.persistence.Table;\n");
			fileWriter.write("\n");
			fileWriter.write(header);
			fileWriter.write("@Entity\n");
			fileWriter.write("@Table(name = " + fileName + ".Columns.TABLE)\n");
			fileWriter.write("public class " + fileName);
			fileWriter.write(" extends BaseEntity {\n");
			fileWriter.write("\tpublic interface Columns {\n");
			fileWriter.write("\t\tString TABLE = \"" + fileName.toLowerCase().charAt(0)
					+ fileName.substring(1, fileName.length()) + "s\";\n");
			fileWriter.write("\t\tString QUERY = \"CREATE TABLE " + fileName.toLowerCase().charAt(0)
					+ fileName.substring(1, fileName.length()) + "s (id INT AUTO_INCREMENT,");
			for (TableData data : fieldsMap) {
				fileWriter.write(data.getName() + " " + data.getDataType());
				if (data.isNotNull()) {
					fileWriter.write(" NOT NULL");
				}
				if (data.isUnique()) {
					fileWriter.write(" UNIQUE");
				}
				fileWriter.write(",");
			}
			fileWriter.write(
					"created_on DATETIME,modified_on DATETIME,created_by INTEGER,modified_by INTEGER,PRIMARY KEY (id));\";\n");
			for (TableData data : fieldsMap) {
				fileWriter.write("\t\tString " + getUppercase(data.getName()) + " = \"" + data.getName() + "\";\n");
			}
			fileWriter.write("\t}\n\n");
			for (TableData data : fieldsMap) {
				fileWriter.write("\t@Column(name = Columns." + getUppercase(data.getName()));
				fileWriter.write(", nullable = " + !data.isNotNull());
				fileWriter.write(", unique = " + !data.isNotNull());
				if (!StringUtils.isEmpty(data.getDataType())) {
					fileWriter.write(", columnDefinition = \"" + data.getDataType() + "\"");
				}

				fileWriter.write(")\n");
				fileWriter.write("\tprivate " + data.getType() + " " + data.getName() + ";\n");
			}
			fileWriter.write("\n");
			createGetter(fieldsMap, fileWriter);
			fileWriter.write("\n");
			createSetter(fieldsMap, fileWriter);
			fileWriter.write("}");
			fileWriter.flush();
			fileWriter.close();
			System.out.println("Entity Created!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getUppercase(String s) {
		char[] charArray = s.toCharArray();
		String upper = "";
		for (char c : charArray) {
			if (c >= 'A' && c <= 'Z') {
				if (upper.length() > 0) {
					upper = upper + "_";
				}
			}
			upper = upper + String.valueOf(c).toUpperCase();
		}
		return upper;
	}

	public static String getMethodeName(String name) {
		return getUppercase(name).charAt(0) + name.substring(1, name.length());
	}
}
