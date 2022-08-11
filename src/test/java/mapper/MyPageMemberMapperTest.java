package mapper;

import domain.MemberVO;
import junit.framework.TestCase;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j
public class MyPageMemberMapperTest extends TestCase {

    @Setter(onMethod_ = @Autowired)
    private MyPageMemberMapper mapper;

    @Test
    public void testRead() {

        MemberVO member = mapper.read(2L);

        log.info(member);

    }

    @Test
    public void testDelete() {

        log.info("delete count: " + mapper.delete(3L));

    }

    @Test
    public void testUpdate() {

        MemberVO member = new MemberVO();

        member.setMemno(1L);  //회원번호가 1인 회원의 회원정보 수정 시도
        member.setNickname("수정");
        member.setPhonenum("수정");
        member.setEmail("수정");
        member.setPw("수정");
        member.setAddress("수정");
        member.setCompanyno("수정");

        int count = mapper.update(member);
        log.info("update count: " + count);  //수정에 실패하면 '0' 출력

    }

}