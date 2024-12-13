import dto.StudentDTO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/student")
public class StudentServlet extends HttpServlet {
    private final List<StudentDTO> studentList = new ArrayList<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        studentList.add(new StudentDTO(1, "Amal", "amal@example.com", 20));
        studentList.add(new StudentDTO(2, "Kamal", "kamal@example.com", 20));
        studentList.add(new StudentDTO(3, "Namal", "namal@example.com", 20));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter printWriter = resp.getWriter();

        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < studentList.size(); i++) {
            StudentDTO studentDTO = studentList.get(i);

            String studentJson = String.format(
                    "{\"id\": %d, \"name\": \"%s\", \"email\": \"%s\", \"age\": %d}",
                    studentDTO.getId(),
                    studentDTO.getName(),
                    studentDTO.getEmail(),
                    studentDTO.getAge()
            );

            stringBuilder.append(studentJson);
            if (i < studentList.size() - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");
        String studentJsonListString = stringBuilder.toString();

        printWriter.write(studentJsonListString);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String name = req.getParameter("name");
        String eMail = req.getParameter("email");
        String age = req.getParameter("age");

        if (name == null || eMail == null || age == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\" : \"Invalid request\"}");
            return;
        }
        try {
            int id = studentList.size() + 1;
            int studentAge = Integer.parseInt(age);

            StudentDTO studentDTO = new StudentDTO(id, name, eMail, studentAge);
            studentList.add(studentDTO);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Age");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\" : \"Invalid age\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        if (id == null || id.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\" : \"Id is required....!\"}");
        } else {
            try {
                int studentId = Integer.parseInt(id);
                StudentDTO studentDTO = findById(studentId);

                if(studentDTO == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\" : \"Student not found\"}");
                } else {
                    studentList.remove(studentDTO);
                    resp.getWriter().write("{\"message\" : \"Student deleted successfully\"}");
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\" : \"Invalid id\"}");
            }

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String eMail = req.getParameter("email");
        String age = req.getParameter("age");

        if (id == null || name == null || eMail == null || age == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\" : \"Invalid request\"}");
        } else{
            try {
                int studentId = Integer.parseInt(id);
                int studentAge = Integer.parseInt(age);

                StudentDTO studentDTO = findById(studentId);
                if(studentDTO == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\" : \"Student not found\"}");
                } else {
                    studentDTO.setName(name);
                    studentDTO.setEmail(eMail);
                    studentDTO.setAge(studentAge);
                    resp.getWriter().write("{\"message\" : \"Student updated successfully\"}");
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\" : \"Invalid id\"}");
            }
        }
    }

    private StudentDTO findById(int id) {
        for (StudentDTO studentDTO : studentList) {
            if (studentDTO.getId() == id) {
                return studentDTO;
            }
        }
        return null;
    }
}
