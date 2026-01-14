package br.com.talles.financecontrol;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import br.com.talles.financecontrol.ui.add.AddDespesaFragment;
import br.com.talles.financecontrol.ui.auth.LoginActivity;
import br.com.talles.financecontrol.ui.categoria.CategoriaFragment;
import br.com.talles.financecontrol.ui.home.HomeFragment;
import br.com.talles.financecontrol.ui.menu.MenuFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        // Fragment inicial (Home)
        carregarFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                carregarFragment(new HomeFragment());
                return true;

            } else if (item.getItemId() == R.id.menu_add) {
                carregarFragment(new AddDespesaFragment());
                return true;

            } else if (item.getItemId() == R.id.menu_categoria) {
                carregarFragment(new CategoriaFragment());
                return true;
            } else if (item.getItemId() == R.id.menu_menu) {
                carregarFragment(new MenuFragment());
                return true;
            }
            return false;
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }

    private void carregarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerFragment, fragment)
                .commit();
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
