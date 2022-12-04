package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    //save()에 @Transactional 걸려있어서 클래스에 안걸어도됨
    @Test
    public void save() {
        //id는 jpa에 퍼시스트하면 안에서 생김
        Item item = new Item("1");
        itemRepository.save(item);
    }
}