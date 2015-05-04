package com.canoo.dolphin.server;

import com.canoo.dolphin.server.impl.BeanBuilder;
import com.canoo.dolphin.server.impl.BeanManagerImpl;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.impl.collections.ListMapper;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.ListReferenceModel;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import com.canoo.dolphin.server.util.SingleReferenceModel;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestModelDeletion extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        manager.remove(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType("simple_test_model");
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        manager.remove(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithSingleReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        manager.remove(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithListReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        manager.remove(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));    //Dolphin Class Repository wurde bereits angelegt

        assertThat(manager.isManaged(model), is(false));
    }

}

