package com.magicento.extensions;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3;
import com.magicento.MagicentoProjectComponent;
import com.magicento.helpers.IdeHelper;
import com.magicento.helpers.MagentoParser;
import com.magicento.helpers.Magicento;
import com.magicento.helpers.PsiPhpHelper;
import com.magicento.models.MagentoClassInfo;
import com.magicento.models.layout.Template;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Enrique Piatti
 */
public class BlockTypeProvider implements PhpTypeProvider3 {

    @Override
    public char getKey() {
        return 0;
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement e)
    {
        if (DumbService.getInstance(e.getProject()).isDumb()) {
            return null;
        }

        if( ! IdeHelper.isPhpWithAutocompleteFeature()){
            return null;
        }

        MagicentoProjectComponent magicentoProjectComponent = MagicentoProjectComponent.getInstance(e.getProject());
        if(magicentoProjectComponent == null || magicentoProjectComponent.isDisabled()){
            return null;
        }

        if( ! magicentoProjectComponent.getMagicentoSettings().automaticThisInTemplate){
            return null;
        }

        // e.isVariable && e.getText.equals("$this");
        // filter out method calls without parameter
        if(!PlatformPatterns.psiElement(PhpElementTypes.VARIABLE).withName("this").accepts(e)) {
            return null;
        }

        if( ! Magicento.isInsideTemplateFile(e)){
            return null;
        }

        final VirtualFile file = e.getContainingFile().getOriginalFile().getVirtualFile();
        Template template = new Template(file);
        List<String> blocks = template.getBlocksClasses(e.getProject());
        if(blocks != null && blocks.size() > 0)
        {
            return new PhpType().add(blocks.get(0));
        }

        return null;

    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Set<String> set, int i, Project project) {
        return null;
    }


}
