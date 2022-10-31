package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                String sql = String.format(
                        "create table if not exists rabbit(%s, %s);",
                        "id serial primary key",
                        "created_date timestamp"
                );
                statement.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connect", connection);
            int interval = Integer.parseInt(intervalProperties().getProperty("rabbit.interval"));
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    private static Connection getConnection() throws Exception {
            Properties config = intervalProperties();
            Class.forName(config.getProperty("driver"));
            return DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("login"),
                    config.getProperty("password")
            );

    }

    private static Properties intervalProperties() throws Exception {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            return config;
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            Connection connect = (Connection) context.getJobDetail().getJobDataMap().get("connect");
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
            try (PreparedStatement ps = connect.prepareStatement("insert into rabbit"
                    + "(created_date) values(?)")) {
                ps.setTimestamp(1, timestamp);
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

/*
SchedulerFactory — интерфейс фабрики для создания Scheduler.
Scheduler — основной класс библиотеки, через который происходит управление планировщиком задач.
Job — интерфейс для создания задач с запланированным выполнением.
JobDetail — интерфейс для создания инстансов Job.
Trigger — интерфейс для определения расписания выполнения задач
JobBuilder и TriggerBuilder — вспомогательные классы для создания инстансов JobDetail и Trigger.
 */
