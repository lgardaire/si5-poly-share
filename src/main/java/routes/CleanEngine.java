package routes;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import tools.util.CloudStorageHelper;
import tools.util.DatastoreHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MultipartConfig
@WebServlet(name = "CleanEngine", value = "/clean")
public class CleanEngine extends HttpServlet {

    private final String BUCKET_NAME = "polyshare-cgjm.appspot.com";

    @Override
    public void init() {
        CloudStorageHelper storageHelper = new CloudStorageHelper();
        this.getServletContext().setAttribute("storageHelper", storageHelper);
        DatastoreHelper datastoreHelper = new DatastoreHelper();
        this.getServletContext().setAttribute("datastoreHelper", datastoreHelper);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatastoreHelper datastoreHelper =  (DatastoreHelper) request.getServletContext().getAttribute("datastoreHelper");
        datastoreHelper.deleteAll();
        CloudStorageHelper storageHelper = (CloudStorageHelper) request.getServletContext().getAttribute("storageHelper");
        storageHelper.deleteAll(BUCKET_NAME);
        Queue queue = QueueFactory.getQueue("queue-casu-leet");
        queue.purge();
        response.getWriter().println("Cleaned databases");
    }
}
