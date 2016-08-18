import scalikejdbc.{AutoSession, ConnectionPool, _}

/*
CREATE TABLE users (
     id MEDIUMINT NOT NULL AUTO_INCREMENT,
     name VARCHAR(255) NOT NULL,
     email VARCHAR(255) NOT NULL,
     PRIMARY KEY (id)
);
*/

case class User(id: Int, name: String, email: String)

object User extends SQLSyntaxSupport[User] {
  override val tableName = "users"
  def apply(rs: WrappedResultSet): User = User(rs.int("id"), rs.string("name"), rs.string("email"))
  def apply(u: ResultName[User])(rs: WrappedResultSet): User = User(rs.int(u.id), rs.string(u.name), rs.string(u.email))
}

object Main {
  def main(args: Array[String]): Unit = {
    val user = "root"
    val pass = "root"
    val host = "localhost:3307"
    val dbname = "testing"

    Class.forName("com.mysql.jdbc.Driver")
    ConnectionPool.singleton("jdbc:mysql://" + host + "/" + dbname + "?characterEncoding=UTF-8", user, pass)

    implicit val session = AutoSession

    // INSERT: 素のSQLに近い書き方
    val alice = User(0, "alice", "alice@example.com")
    val bob = User(0, "bob", "bob@example.com")
    sql"INSERT INTO users (name, email) VALUES (${alice.name}, ${alice.email})".update().apply()
    sql"INSERT INTO users (name, email) VALUE  (${bob.name}, ${bob.email})".update().apply()

    // INSERT: DSLを使った書き方
    val uc = User.column
    withSQL { insert.into(User).namedValues(uc.name -> "carol", uc.email -> "carol@example.com") }.update.apply()

    // SELECT: 素のSQLに近い書き方
    val users = sql"SELECT * FROM users".map(User(_)).list.apply()
    println(users)

    // SELECT: DSLを使った書き方
    val u = User.syntax("u")
    val users2 = withSQL { select.from(User as u) }.map(User(u.resultName)).list().apply()
    println(users2)

    // SELECT: WHEREで絞り込む
    val users3 = withSQL { select.from(User as u).where.eq(u.name, "bob") }.map(User(u.resultName)).list().apply()
    println(users3)
  }
}

