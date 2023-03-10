/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.cafe_management_app;

import com.mycompany.cafe_management_app.config.HibernateConfig;
import com.mycompany.cafe_management_app.model.Account;
import com.mycompany.cafe_management_app.model.Staff;
import com.mycompany.cafe_management_app.controller.LoginController;
import com.mycompany.cafe_management_app.dao.StaffDao;
import com.mycompany.cafe_management_app.service.StaffService;
import com.mycompany.cafe_management_app.util.ClientUtil;
import com.mycompany.cafe_management_app.util.ErrorUtil;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.time.LocalDate;

/**
 * @author Hieu
 */
public class Main {

    public static void main(String[] args) {
        String appID = "my_app_id";
        boolean alreadyRunning;
        try {
            JUnique.acquireLock(appID);
            alreadyRunning = false;

        } catch (AlreadyLockedException e) {
            alreadyRunning = true;
        }

        if (!alreadyRunning) {
//            Init Hibernate and ErrorUtil
            HibernateConfig.getSessionFactory();
            ErrorUtil.getInstance();

//            Init ClientUtil
            ClientUtil.getInstance();

//            Test make transaction async
            StaffService st = new StaffService();
            st.makeTransactionAsync(null, null)
                    .thenApply(res -> {
                        System.out.println(res);
                        return res;
                    });

//            Create admin account if not exist
            initAdmin();

//            Show login UI
            LoginController loginController = new LoginController();
            loginController.getLoginUI().setVisible(true);
        }
    }

    private static void initAdmin() {
        StaffDao staffDao = new StaffDao();
        if (staffDao.getAll().isEmpty()) {
            Staff s = new Staff(
                    "Hieu",
                    LocalDate.of(2002, 6, 1),
                    "012345679",
                    "admin");
            Account a = new Account("admin", "admin", "admin");
            s.setAccount(a);
            staffDao.save(s);
        }
    }
}
